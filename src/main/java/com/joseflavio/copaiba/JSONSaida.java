
/*
 *  Copyright (C) 2016-2021 José Flávio de Souza Dias Júnior
 *  
 *  This file is part of Copaíba - <http://joseflavio.com/copaiba/>.
 *  
 *  Copaíba is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Copaíba is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Copaíba. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *  Direitos Autorais Reservados (C) 2016-2021 José Flávio de Souza Dias Júnior
 * 
 *  Este arquivo é parte de Copaíba - <http://joseflavio.com/copaiba/>.
 * 
 *  Copaíba é software livre: você pode redistribuí-lo e/ou modificá-lo
 *  sob os termos da Licença Pública Menos Geral GNU conforme publicada pela
 *  Free Software Foundation, tanto a versão 3 da Licença, como
 *  (a seu critério) qualquer versão posterior.
 * 
 *  Copaíba é distribuído na expectativa de que seja útil,
 *  porém, SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  COMERCIABILIDADE ou ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a
 *  Licença Pública Menos Geral do GNU para mais detalhes.
 * 
 *  Você deve ter recebido uma cópia da Licença Pública Menos Geral do GNU
 *  junto com Copaíba. Se não, veja <http://www.gnu.org/licenses/>.
 */

package com.joseflavio.copaiba;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseflavio.copaiba.JSONEntrada.JSONBinario;
import com.joseflavio.urucum.json.JSONUtil;

/**
 * {@link Saida} baseada em {@link ObjectMapper}.
 * @author José Flávio de Souza Dias Júnior
 */
class JSONSaida implements Saida {
	
	private Writer saida;
	
	private ObjectMapper conversor;
	
	public JSONSaida( OutputStream os ) throws IOException {
		this.saida = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );
		this.conversor = JSONUtil.novoConversor();
	}
	
	private void texto( char[] o, boolean aspas ) throws IOException {
		for( int i = 0; i < o.length; i++ ){
			if( o[i] == JSONEntrada.SEPARADOR ) o[i] = ' ';
		}
		if( aspas ){
			saida.write( '"' );
			saida.write( o );
			saida.write( '"' );
		}else{
			saida.write( o );
		}
		saida.write( JSONEntrada.SEPARADOR );
		saida.flush();
	}
	
	@Override
	public void objeto( Serializable o ) throws IOException {
		
		if( o == null ){
			texto( "null" );
			
		}else if( o.getClass().isArray() ){
			texto( "vetor" );
			Class<?> classe = o.getClass();
			while( classe.isArray() ) classe = classe.getComponentType();
			texto( classe.getName() );
			int total = Array.getLength( o );
			inteiro32( total );
			for( int i = 0; i < total; i++ ){
				objeto( (Serializable) Array.get( o, i ) );
			}
			
		}else if( o instanceof List ){
			texto( "lista" );
			List<?> lista = (List<?>) o;
			inteiro32( lista.size() );
			for( Object s : lista ) objeto( (Serializable) s );
			
		}else if( o instanceof Set ){
			texto( "conjunto" );
			Set<?> conjunto = (Set<?>) o;
			inteiro32( conjunto.size() );
			for( Object s : conjunto ) objeto( (Serializable) s );
			
		}else if( o instanceof Map ){
			texto( "mapa" );
			Map<?,?> mapa = (Map<?,?>) o;
			inteiro32( mapa.size() );
			for( Object s : mapa.keySet() ){
				objeto( (Serializable) s );
				objeto( (Serializable) mapa.get( s ) );
			}
			
		}else{
			texto( o.getClass().getName() );
			texto( conversor.writeValueAsString( o ).toCharArray(), false );
		}
		
	}
	
	@Override
	public void texto( String o ) throws IOException {
		if( o == null ) o = "_$_COPAIBA_TEXTO_NULO_$_";
		texto( o.toCharArray(), true );
	}

	@Override
	public void inteiro8( byte o ) throws IOException {
		inteiro64( o );
	}

	@Override
	public void inteiro16( short o ) throws IOException {
		inteiro64( o );
	}

	@Override
	public void inteiro32( int o ) throws IOException {
		inteiro64( o );
	}

	@Override
	public void inteiro64( long o ) throws IOException {
		saida.write( Long.toString( o ) );
		saida.write( JSONEntrada.SEPARADOR );
		saida.flush();
	}

	@Override
	public void real32( float o ) throws IOException {
		real64( o );
	}

	@Override
	public void real64( double o ) throws IOException {
		saida.write( Double.toString( o ) );
		saida.write( JSONEntrada.SEPARADOR );
		saida.flush();
	}

	@Override
	public void caractere( char o ) throws IOException {
		texto( new char[]{ o }, true );
	}

	@Override
	public void logico( boolean o ) throws IOException {
		saida.write( o ? "true" : "false" );
		saida.write( JSONEntrada.SEPARADOR );
		saida.flush();
	}

	@Override
	public void bytes( byte[] o, int inicio, int total ) throws IOException {
		JSONBinario binario = new JSONBinario( new byte[total] );
		System.arraycopy( o, inicio, binario.conteudo, 0, total );
		objeto( binario );
	}
	
	@Override
	public void comando( Comando o ) throws IOException {
		inteiro8( o.getCodigo() );
	}

}

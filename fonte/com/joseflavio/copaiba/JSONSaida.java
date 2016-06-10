
/*
 *  Copyright (C) 2016 Jos� Fl�vio de Souza Dias J�nior
 *  
 *  This file is part of Copa�ba - <http://www.joseflavio.com/copaiba/>.
 *  
 *  Copa�ba is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Copa�ba is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Copa�ba. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *  Direitos Autorais Reservados (C) 2016 Jos� Fl�vio de Souza Dias J�nior
 * 
 *  Este arquivo � parte de Copa�ba - <http://www.joseflavio.com/copaiba/>.
 * 
 *  Copa�ba � software livre: voc� pode redistribu�-lo e/ou modific�-lo
 *  sob os termos da Licen�a P�blica Menos Geral GNU conforme publicada pela
 *  Free Software Foundation, tanto a vers�o 3 da Licen�a, como
 *  (a seu crit�rio) qualquer vers�o posterior.
 * 
 *  Copa�ba � distribu�do na expectativa de que seja �til,
 *  por�m, SEM NENHUMA GARANTIA; nem mesmo a garantia impl�cita de
 *  COMERCIABILIDADE ou ADEQUA��O A UMA FINALIDADE ESPEC�FICA. Consulte a
 *  Licen�a P�blica Menos Geral do GNU para mais detalhes.
 * 
 *  Voc� deve ter recebido uma c�pia da Licen�a P�blica Menos Geral do GNU
 *  junto com Copa�ba. Se n�o, veja <http://www.gnu.org/licenses/>.
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
import com.joseflavio.copaiba.util.CopaibaUtil;

/**
 * {@link Saida} baseada em {@link ObjectMapper}.
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
class JSONSaida implements Saida {
	
	private Writer saida;
	
	private ObjectMapper conversor;
	
	public JSONSaida( OutputStream os ) throws IOException {
		this.saida = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );
		this.conversor = CopaibaUtil.novoConversorJSON();
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

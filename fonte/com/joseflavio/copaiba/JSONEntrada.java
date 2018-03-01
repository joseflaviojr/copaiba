
/*
 *  Copyright (C) 2016-2018 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2018 José Flávio de Souza Dias Júnior
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseflavio.urucum.json.JSONUtil;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * {@link Entrada} baseada em {@link ObjectMapper}.
 * @author José Flávio de Souza Dias Júnior
 */
class JSONEntrada implements Entrada {
	
	static final char SEPARADOR = '\b';
	
	private Reader entrada;
	
	private ObjectMapper conversor;
	
	public JSONEntrada( InputStream is ) throws IOException {
		this.entrada = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
		this.conversor = JSONUtil.novoConversor();
	}
	
	private StringBuilder proximo() throws IOException {
		StringBuilder conteudo = new StringBuilder( 128 );
		char c;
		while( ( c = (char) entrada.read() ) != (char) -1 ){
			if( c == SEPARADOR ){
				return conteudo;
			}else{
				conteudo.append( c );
			}
		}
		return conteudo;
	}
	
	private void incompativel() throws IOException {
		throw new IOException( "JSON incompatível." );
	}
	
	private Class<?> classe() throws IOException, ClassNotFoundException {
		String nome = texto();
		if( nome.equals( "byte"    ) ) return byte.class;
		if( nome.equals( "short"   ) ) return short.class;
		if( nome.equals( "int"     ) ) return int.class;
		if( nome.equals( "long"    ) ) return long.class;
		if( nome.equals( "float"   ) ) return float.class;
		if( nome.equals( "double"  ) ) return double.class;
		if( nome.equals( "char"    ) ) return char.class;
		if( nome.equals( "boolean" ) ) return boolean.class;
		return Class.forName( nome );
	}

	@Override
	public Serializable objeto() throws IOException {
		
		String tipo = texto();
		
		if( tipo.equals( "null" ) ){
			return null;
		
		}else if( tipo.equals( "vetor" ) ){
			try{
				Class<?> classe = classe();
				int total = inteiro32();
				Serializable vetor = (Serializable) Array.newInstance( classe, total );
				for( int i = 0; i < total; i++ ) Array.set( vetor, i, objeto() );
				return vetor;
			}catch( ClassNotFoundException e ){
				throw new IOException( e );
			}
			
		}else if( tipo.equals( "lista" ) ){
			int total = inteiro32();
			ArrayList<Serializable> lista = new ArrayList<Serializable>( total );
			for( int i = 0; i < total; i++ ) lista.add( objeto() );
			return lista;
			
		}else if( tipo.equals( "conjunto" ) ){
			int total = inteiro32();
			HashSet<Serializable> conjunto = new HashSet<Serializable>( total );
			for( int i = 0; i < total; i++ ) conjunto.add( objeto() );
			return conjunto;
			
		}else if( tipo.equals( "mapa" ) ){
			int total = inteiro32();
			HashMap<Serializable,Serializable> conjunto = new HashMap<Serializable,Serializable>( total );
			for( int i = 0; i < total; i++ ) conjunto.put( objeto(), objeto() );
			return conjunto;
			
		}else{
			try{
				return (Serializable) conversor.readValue( proximo().toString(), Class.forName( tipo ) );
			}catch( IOException e ){
				throw e;
			}catch( Exception e ){
				throw new IOException( e );
			}
		}
		
	}

	@Override
	public String texto() throws IOException {
		StringBuilder valor = proximo();
		if( valor.length() < 2 ) incompativel();
		String texto = valor.substring( 1, valor.length() - 1 );
		return texto.equals( "_$_COPAIBA_TEXTO_NULO_$_" ) ? null : texto;
	}

	@Override
	public byte inteiro8() throws IOException {
		return (byte) inteiro64();
	}

	@Override
	public short inteiro16() throws IOException {
		return (short) inteiro64();
	}

	@Override
	public int inteiro32() throws IOException {
		return (int) inteiro64();
	}

	@Override
	public long inteiro64() throws IOException {
		try{
			return Long.parseLong( proximo().toString() );
		}catch( NumberFormatException e ){
			throw new IOException( e );
		}
	}

	@Override
	public float real32() throws IOException {
		return (float) real64();
	}

	@Override
	public double real64() throws IOException {
		try{
			return Double.parseDouble( proximo().toString() );
		}catch( NumberFormatException e ){
			throw new IOException( e );
		}
	}

	@Override
	public char caractere() throws IOException {
		String texto = texto();
		if( texto == null || texto.length() != 1 ) incompativel();
		return texto.charAt( 0 );
	}

	@Override
	public boolean logico() throws IOException {
		String valor = proximo().toString();
		if( valor.equals( "true"  ) ) return true;
		if( valor.equals( "false" ) ) return false;
		incompativel();
		return false;
	}

	@Override
	public void bytes( byte[] o, int inicio, int total ) throws IOException {
		JSONBinario obj = (JSONBinario) objeto();
		if( obj == null || obj.conteudo == null || obj.conteudo.length != total ) incompativel();
		System.arraycopy( obj.conteudo, 0, o, inicio, total );
	}
	
	@Override
	public Comando comando() throws IOException {
		return Comando.getComando( inteiro8() );
	}
	
	@Override
	public void saltarObjeto() throws IOException {
		proximo();
	}
	
	@Override
	public void saltarTexto() throws IOException {
		proximo();
	}
	
	/**
	 * @see JsonNode#isBinary()
	 */
	public static final class JSONBinario implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		byte[] conteudo;
		
		public JSONBinario() {
		}

		public JSONBinario( byte[] conteudo ) {
			this.conteudo = conteudo;
		}
		
		public byte[] getConteudo() {
			return conteudo;
		}
		
		public void setConteudo( byte[] conteudo ) {
			this.conteudo = conteudo;
		}
		
	}
	
}

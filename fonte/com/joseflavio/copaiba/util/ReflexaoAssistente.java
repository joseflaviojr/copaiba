
/*
 *  Copyright (C) 2016-2019 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2019 José Flávio de Souza Dias Júnior
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

package com.joseflavio.copaiba.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.joseflavio.copaiba.Assistente;

/**
 * {@link Assistente} baseado em <i>java.lang.reflect</i>.
 * @author José Flávio de Souza Dias Júnior
 * @see Class
 * @see Field
 * @see Method
 */
public class ReflexaoAssistente implements Assistente {

	private Set<String> classes = Collections.synchronizedSet( new HashSet<String>( 512 ) );
	
	private List<Javadoc> javadocs = Collections.synchronizedList( new ArrayList<Javadoc>() );
	
	public ReflexaoAssistente() {
	}
	
	/**
	 * @see #mais(Collection)
	 */
	public ReflexaoAssistente( Collection<String> classes ) {
		mais( classes );
	}
	
	/**
	 * @see #mais(String...)
	 */
	public ReflexaoAssistente( String... classes ) {
		mais( classes );
	}
	
	/**
	 * @see #mais(File...)
	 */
	public ReflexaoAssistente( File... classes ) throws IOException {
		mais( classes );
	}
	
	/**
	 * Adiciona nomes de classes ao conjunto de {@link #getClasses()}. 
	 */
	public void mais( Collection<String> classes ) {
		for( String classe : classes ) this.classes.add( classe );
	}
	
	/**
	 * Adiciona nomes de classes ao conjunto de {@link #getClasses()}. 
	 */
	public void mais( String... classes ) {
		for( String classe : classes ) this.classes.add( classe );
	}
	
	/**
	 * Adiciona os nomes das classes que estão armazenadas em {@link File arquivos}.<br>
	 * Os nomes das classes serão compostos conforme o endereço relativo dos arquivos ".java" e ".class".<br>
	 * Exemplo: [Arquivo.zip]/com/joseflavio/copaiba/Copaiba.class = "com.joseflavio.copaiba.Copaiba"
	 * @param classes Arquivos ".jar", ".zip" e/ou {@link File#isDirectory() diretórios}.
	 */
	public void mais( File... classes ) throws IOException {
		for( File arquivo : classes ){
			if( arquivo.isDirectory() ) processarDiretorio( arquivo, arquivo.getAbsolutePath() );
			else if( arquivo.getName().toLowerCase().endsWith( ".jar" ) ) processarZip( arquivo );
			else if( arquivo.getName().toLowerCase().endsWith( ".zip" ) ) processarZip( arquivo );
		}
	}
	
	public void mais( Javadoc javadoc ) {
		if( javadoc == null ) throw new IllegalArgumentException();
		this.javadocs.add( javadoc );
	}
	
	/**
	 * @see #mais(Javadoc)
	 * @see Javadoc#Javadoc(String, String)
	 */
	public void mais( String url, String prefixo ) throws MalformedURLException {
		mais( new Javadoc( url, prefixo ) );
	}
	
	private void processarDiretorio( File diretorio, String raiz ) throws IOException {
		for( File arquivo : diretorio.listFiles() ){
			if( arquivo.isDirectory() ) processarDiretorio( arquivo, raiz );
			else processarJava( arquivo.getAbsolutePath(), raiz );
		}
	}
	
	private void processarJava( String endereco, String raiz ) throws IOException {
		if( endereco.endsWith( ".java" ) || endereco.endsWith( ".class" ) ){
			String nome = endereco.substring( raiz.length() + 1, endereco.lastIndexOf( '.' ) );
			nome = nome.replace( '/', '.' );
			nome = nome.replace( '\\', '.' );
			classes.add( nome );
		}
	}
	
	private void processarZip( File zip ) throws IOException {
		ZipInputStream entrada = null;
		try{
			entrada = new ZipInputStream( new FileInputStream( zip ) );
			ZipEntry je;
			while( ( je = entrada.getNextEntry() ) != null ){
				if( ! je.isDirectory() ){
					processarJava( "/" + je.getName(), "" );
				}
				entrada.closeEntry();
			}
		}finally{
			try{
				if( entrada != null ) entrada.close();
			}catch( Exception e ){
			}
		}
	}
	
	@Override
	public String[] getClasses() {
		return classes.toArray( new String[0] );
	}
	
	@Override
	public boolean contemClasse( String classe ) {
		return classes.contains( classe );
	}

	@Override
	public String[] getMembros( String classe ) {
		try{
			List<String> membros = new ArrayList<String>( 100 );
			getMembros( Class.forName( classe ), membros );
			return membros.toArray( new String[0] );
		}catch( ClassNotFoundException e ){
			return null;
		}
	}
	
	private static void getMembros( Class<?> classe, List<String> membros ) {
		
		// Atributos
		
		for( Field a : classe.getFields() ){
			membros.add( a.getDeclaringClass().getName() + "@" + a.getName() );
		}
		
		// Métodos
		
		for( Method m : classe.getMethods() ){
			StringBuilder sb = new StringBuilder( m.getDeclaringClass().getName() + "#" + m.getName() + "(" );
			boolean primeiro = true;
			for( Parameter p : m.getParameters() ){
				Class<?> tipo = p.getType();
				if( primeiro ){
					primeiro = false;						
				}else{
					sb.append( "," );
				}
				if( tipo.isArray() ){
					sb.append( tipo.getComponentType().getName() );
					sb.append( p.isVarArgs() ? "..." : "[]" );
				}else{
					sb.append( tipo.getName() );
				}
			}
			sb.append( ")" );
			membros.add( sb.toString() );
		}
		
		// Classes internas
		
		for( Class<?> c : classe.getClasses() ){
			membros.add( c.getDeclaringClass().getName() + "&" + c.getSimpleName() );
		}
		
	}
	
	@Override
	public URL getDocumentacao( String componente ) {
		
		if( componente == null ) throw new IllegalArgumentException();
		
		for( Javadoc javadoc : javadocs ){
			
			if( componente.startsWith( javadoc.getPrefixo() ) ){
				
				StringBuilder url = new StringBuilder( javadoc.getURL().toString() );
				if( url.charAt( url.length() - 1 ) != '/' ) url.append( '/' );
				
				if( componente.contains( "@" ) ){
					
					componente = componente.replace( '.', '/' );
					componente = componente.replace( "@", ".html#" );
					url.append( componente );
					
				}else if( componente.contains( "#" ) ){
					
					String[] parte = componente.split( "#" );
					url.append( parte[0].replace( '.', '/' ) + ".html#" );
					url.append( parte[1].replaceAll( "[(),]", "-" ).replace( "[]", ":A" ) );
					
				}else if( componente.contains( "&" ) ){
					
					componente = componente.replace( '.', '/' );
					componente = componente.replace( '&', '.' );
					url.append( componente + ".html" );
					
				}else{
					
					url.append( componente.replace( '.', '/' ) + ".html" );
					
				}
				
				try{
					return new URL( url.toString() );
				}catch( MalformedURLException e ){
					return null;
				}
				
			}
			
		}
		
		return null;
		
	}
	
}


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

package com.joseflavio.copaiba.teste;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.joseflavio.copaiba.Auditor;
import com.joseflavio.copaiba.Autenticador;
import com.joseflavio.copaiba.Copaiba;
import com.joseflavio.urucum.comunicacao.DiretoServidor;

/**
 * {@link Copaiba#abrir(com.joseflavio.urucum.comunicacao.Servidor) Abre} dois {@link Copaiba}s para fins de testes.
 * @author José Flávio de Souza Dias Júnior
 */
class TesteServidores {
	
	public static final int PORTA_NORMAL = 1024;
	public static final int PORTA_SEGURA = 1025;
	
	Copaiba servidorNormal;
	
	Copaiba servidorSeguro;
	
	Copaiba servidorDireto;
	
	static {
		configurarKeyStore();
		new TesteSeguranca().ativar();
	}
	
	public TesteServidores() {
		
		Autenticador  autenticador  = new TesteServidoresAutenticador();
		Auditor       auditor       = new TesteServidoresAuditor();
		
		//Servidor sem TLS
		
		servidorNormal = new Copaiba( autenticador, null, auditor, null );
		servidorNormal.setFornecedor( new TesteServidoresFornecedor( servidorNormal ) );
		
		new Thread(){
			@Override
			public void run() {
				try{
					servidorNormal.abrir( PORTA_NORMAL, false );
				}catch( Exception e ){
					CopaibaBaseTeste.excecaoIntraThread = e;
				}
			}
		}.start();
		
		//Servidor com TLS
		
		servidorSeguro = new Copaiba( autenticador, null, auditor, null );
		servidorSeguro.setFornecedor( new TesteServidoresFornecedor( servidorSeguro ) );
		
		new Thread(){
			@Override
			public void run() {
				try{
					servidorSeguro.abrir( PORTA_SEGURA, true );
				}catch( Exception e ){
					CopaibaBaseTeste.excecaoIntraThread = e;
				}
			}
		}.start();
		
		//Servidor Direto
		
		servidorDireto = new Copaiba( autenticador, null, auditor, null );
		servidorDireto.setFornecedor( new TesteServidoresFornecedor( servidorDireto ) );
		
		new Thread(){
			@Override
			public void run() {
				try{
					servidorDireto.abrir( new DiretoServidor() );
				}catch( Exception e ){
					CopaibaBaseTeste.excecaoIntraThread = e;
				}
			}
		}.start();
		
	}
	
	public void fechar() {
		
		if( servidorNormal != null && servidorNormal.isAberta() ){
			servidorNormal.fechar();
			servidorNormal = null;
		}
		
		if( servidorSeguro != null && servidorSeguro.isAberta() ){
			servidorSeguro.fechar();
			servidorSeguro = null;
		}
		
	}
	
	private static void configurarKeyStore() {
		
		try{
			
			File keyStore = File.createTempFile( "keyStore", ".jks" );
			copiar( CopaibaBaseTeste.class.getResourceAsStream( "/servidor.jks" ), new FileOutputStream( keyStore ) );
			System.setProperty( "javax.net.ssl.keyStore", keyStore.getAbsolutePath() );
			System.setProperty( "javax.net.ssl.keyStorePassword", "123456" );
			
			File trustStore = File.createTempFile( "trustStore", ".jks" );
			copiar( CopaibaBaseTeste.class.getResourceAsStream( "/cliente.jks" ), new FileOutputStream( trustStore ) );
			System.setProperty( "javax.net.ssl.trustStore", trustStore.getAbsolutePath() );
			System.setProperty( "javax.net.ssl.trustStorePassword", "123456" );
			
		}catch( Exception e ){
			CopaibaBaseTeste.excecaoIntraThread = e;
		}
		
	}
	
	private static void copiar( InputStream origem, OutputStream destino ) throws IOException {
		int c;
		while( ( c = origem.read() ) != -1 ) destino.write( c );
		origem.close();
		destino.close();
	}
	
}

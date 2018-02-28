
/*
 *  Copyright (C) 2016-2018 Jos� Fl�vio de Souza Dias J�nior
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
 *  Direitos Autorais Reservados (C) 2016-2018 Jos� Fl�vio de Souza Dias J�nior
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
 * @author Jos� Fl�vio de Souza Dias J�nior
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

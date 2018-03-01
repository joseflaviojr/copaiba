
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

package com.joseflavio.copaiba.teste;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.joseflavio.copaiba.Copaiba;
import com.joseflavio.copaiba.CopaibaConexao;
import com.joseflavio.copaiba.CopaibaException;
import com.joseflavio.copaiba.Erro;
import com.joseflavio.copaiba.Informacao;
import com.joseflavio.urucum.comunicacao.Consumidor;

/**
 * {@link Test} base geral da {@link Copaiba}.
 * @author José Flávio de Souza Dias Júnior
 */
@FixMethodOrder(MethodSorters.JVM)
public abstract class CopaibaBaseTeste {
	
	protected static TesteServidores servidores;
	
	static Throwable excecaoIntraThread;
	
	protected abstract Consumidor novoConsumidor() throws IOException;
	
	protected CopaibaConexao novaCopaibaConexao( String usuario, String senha ) throws CopaibaException {
		try{
			return new CopaibaConexao( novoConsumidor(), usuario, senha );
		}catch( IOException e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
	}
	
	@BeforeClass
	public static void inicializar() throws IOException {
		
		servidores = new TesteServidores();
		
	}
	
	@AfterClass
	public static void finalizar() throws IOException {
		
		servidores.fechar();
		servidores = null;
		
	}
	
	@Test
	public void testarInformacao() throws
					CopaibaException, IOException,
					KeyStoreException, CertificateException,
					NoSuchAlgorithmException {
		
		// Conexão
		
		Consumidor consumidor = novoConsumidor();
		Informacao info = CopaibaConexao.obterInformacao( consumidor );

		Assert.assertFalse( consumidor.isAberto() );
		
		// Versão
		
		Assert.assertEquals( Copaiba.VERSAO, info.getVersao(), 0 );
		
		// Certificados
		
		File jks = new File( System.getProperty( "javax.net.ssl.keyStore" ) );
		String jksSenha = System.getProperty( "javax.net.ssl.keyStorePassword" );
		
		KeyStore ks = KeyStore.getInstance( KeyStore.getDefaultType() );
		ks.load( new FileInputStream( jks ), jksSenha.toCharArray() );

		Collection<Certificate> certificados1 = info.getCertificados();
		Collection<Certificate> certificados2 = new HashSet<Certificate>();
		for( String nome : Collections.list( ks.aliases() ) ){
			certificados2.add( ks.getCertificate( nome ) );
		}
		
		Assert.assertEquals( certificados1, certificados2 );
		
		// Linguagens
		
		Collection<String> linguagens1 = info.getLinguagens();
		Collection<String> linguagens2 = new HashSet<String>();
		for( ScriptEngineFactory sef : new ScriptEngineManager().getEngineFactories() ){
			linguagens2.add( sef.getLanguageName() );
		}
		
		Assert.assertEquals( linguagens1, linguagens2 );
		
	}

	@Test
	public void testarConexao() throws CopaibaException {
		
		CopaibaConexao copaiba1 = novaCopaibaConexao( "jose", "1234" );
		Assert.assertTrue( ((String)copaiba1.executar( "pessoa.nome" )).equals( "José Teste" ) );
		verificarFechamento( copaiba1 );
		
		CopaibaConexao copaiba2 = novaCopaibaConexao( "maria", "4321" );
		Assert.assertTrue( ((String)copaiba2.executar( "pessoa.nome" )).equals( "Maria Teste" ) );
		verificarFechamento( copaiba2 );
		
	}
	
	@Test(expected=CopaibaException.class)
	public void testarFalhaAutenticacao() throws CopaibaException {
		novaCopaibaConexao( "jose", "12345" );
	}
	
	@Test
	public void testarExecucao() throws CopaibaException {
		
		CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );
		
		copaiba.executar( "Groovy", "x = 123.45;", null );
		Object x = copaiba.executar( "Groovy", "x", null );
		Assert.assertTrue( x instanceof Number );
		Assert.assertEquals( 123.45d, ((Number)x).doubleValue(), 0d );
		
		StringWriter saida = new StringWriter();
		x = copaiba.executar( "Groovy", "\n\nprintln(x);x++;println(x)\nprintln(x+0.1)\nx\n", saida );
		Assert.assertEquals( "123.45\n124.45\n124.55\n", saida.toString() );
		Assert.assertEquals( 124.45d, ((Number)x).doubleValue(), 0d );
		
		x = copaiba.executar( "Groovy", "x", null, true );
		Assert.assertEquals( "124.45", x );
		
		verificarFechamento( copaiba );
		
	}
	
	@Test
	public void testarAtribuicao() throws CopaibaException {
		
		CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );
		
		//Pessoa
		
		TestePessoa pessoa = new TestePessoa(
			"José Flávio de Souza Dias Júnior",
			new Date(),
			new TestePessoa( "Cecília Maria Tavares Dias", new Date(), null )
		);
		
		copaiba.atribuir( "teste", pessoa );
		
		String nome = (String) copaiba.executar( "teste.nome" );
		Assert.assertEquals( nome, pessoa.getNome() );
		
		String nomeMae = (String) copaiba.executar( "teste.mae.nome" );
		Assert.assertEquals( nomeMae, pessoa.getMae().getNome() );
		
		TestePessoa pessoa2 = (TestePessoa) copaiba.obter( "teste" );
		Assert.assertEquals( pessoa, pessoa2 );
		
		//null
		
		copaiba.atribuir( "nulo", null );
		Assert.assertEquals( null, copaiba.obter( "nulo" ) );
		Assert.assertEquals( true, copaiba.executar( "nulo == null" ) );
		
		//int
		
		copaiba.atribuir( "inteiro", 21 );
		Assert.assertEquals( 21, ((Number)copaiba.executar( "inteiro" )).intValue() );
		
		//String
		
		copaiba.atribuir( "texto", "José Flávio" );
		Assert.assertEquals( "José Flávio", copaiba.executar( "texto" ) );
		
		//Vetor
		
		byte[] bytes = { 2, 5, 0, 4, 1, 9, 8, 5 };
		copaiba.atribuir( "bytes", bytes );
		byte[] bytes2 = (byte[]) copaiba.obter( "bytes" );
		Assert.assertTrue( Arrays.equals( bytes, bytes2 ) );
		
		//Lista
		
		ArrayList<Integer> lista = new ArrayList<Integer>();
		for( int i = 1; i < 100; i++ ) lista.add( i );
		copaiba.atribuir( "lista", lista );
		List<?> lista2 = (List<?>) copaiba.obter( "lista" );
		Assert.assertEquals( lista, lista2 );
		
		//Conjunto
		
		HashSet<Integer> conjunto = new HashSet<Integer>();
		for( int i = 1; i < 100; i++ ) conjunto.add( i );
		copaiba.atribuir( "conjunto", conjunto );
		Set<?> conjunto2 = (Set<?>) copaiba.obter( "conjunto" );
		Assert.assertEquals( conjunto, conjunto2 );
		
		//Mapa
		
		HashMap<Integer,String> mapa = new HashMap<Integer,String>();
		for( int i = 1; i < 100; i++ ) mapa.put( i, "valor" + i );
		copaiba.atribuir( "mapa", mapa );
		Map<?,?> mapa2 = (Map<?,?>) copaiba.obter( "mapa" );
		Assert.assertEquals( mapa, mapa2 );
		
		//JSON
		
		Calendar cal = Calendar.getInstance();
		cal.set( 1985, 4, 25, 20, 0, 0 );
		
		String json = "{\"nome\":\"José Flávio\",\"nascimento\":" + cal.getTimeInMillis() + ",\"mae\":null}";
		
		copaiba.atribuir(
			"json",
			TestePessoa.class.getName(),
			json
		);
		
		Assert.assertEquals(
			new TestePessoa( "José Flávio", cal.getTime(), null ),
			copaiba.obter( "json" )
		);
		
		Assert.assertEquals(
			json,
			copaiba.obter( "json", true )
		);
		
		Assert.assertEquals(
			"\"José Flávio\"",
			copaiba.obter( "json", "getNome", true )
		);
		
		//Fim
		
		verificarFechamento( copaiba );
		
	}
	
	@Test
	public void testarObtencao() throws CopaibaException {
		
		CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );
		
		Assert.assertEquals( "José Teste", copaiba.obter( "pessoa", "getNome", false ) );
		
		Assert.assertEquals( 16.8d, ((Number)copaiba.obter( "compartilhado", "somar", false, 12.3d, 4.5d )).doubleValue(), 0d );
		
		Calendar cal = Calendar.getInstance();
		int anoAtual = cal.get( Calendar.YEAR );
		cal.set( 1985, 4, 25, 0, 0, 0 );
		TestePessoa p1 = new TestePessoa( "A", cal.getTime(), null );
		cal.set( 1991, 1, 2, 0, 0, 0 );
		TestePessoa p2 = new TestePessoa( "B", cal.getTime(), null );
		int resultado = ((Number)copaiba.obter( "compartilhado", "somarIdades", false, p1, p2 )).intValue();
		Assert.assertEquals( ((anoAtual-1985)+(anoAtual-1991)), resultado );
		
		copaiba.atribuir( "inteiro", 21 );
		copaiba.atribuir( "texto", "José Flávio" );
		Assert.assertEquals( 21, ((Number)copaiba.obter( "inteiro" )).intValue() );
		Assert.assertEquals( "José Flávio", copaiba.obter( "texto" ) );
		
		verificarFechamento( copaiba );
		
	}
	
	@Test(expected=CopaibaException.class)
	public void testarNaoSerializavel() throws CopaibaException {
		CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );
		copaiba.executar( "compartilhado" );
		verificarFechamento( copaiba );
	}
	
	@Test
	public void testarResiliencia() throws CopaibaException {
		CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );
		try{
			copaiba.executar( "dlfwoeyrb237o2" ); //inexistente
			Assert.fail();
		}catch( Exception e ){
		}
		Assert.assertTrue( ((String)copaiba.executar( "pessoa.nome" )).equals( "José Teste" ) );
		try{
			copaiba.executar( "compartilhado" ); //não serializável
			Assert.fail();
		}catch( Exception e ){
		}
		Assert.assertTrue( ((String)copaiba.executar( "pessoa.nome" )).equals( "José Teste" ) );
		verificarFechamento( copaiba );
	}
	
	@Test
	public void testarTransferencia() throws CopaibaException, IOException {

		File tmpDir = new File( System.getProperty( "user.home" ) + File.separator + "tmp" );
		tmpDir.mkdir();
		
		File arquivo1 = File.createTempFile( "copaiba.", ".tmp", tmpDir );
		FileOutputStream fos = new FileOutputStream( arquivo1 );
		for( int i = 0; i < 1024 * 100; i++ ){
			fos.write( ( i % 26 ) + 'A' );
		}
		fos.close();
		
		CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );

		copaiba.executar( "Groovy", "arquivo = File.createTempFile( 'copaiba.', '.tmp', new File( '" + tmpDir.getAbsolutePath() + "' ) )", null );
		UUID id = (UUID) copaiba.executar( "Groovy", "copaiba_servico.registrarTransferencia( arquivo, null, null )", null );
		Assert.assertNotNull( id );
		Consumidor consumidor = novoConsumidor();
		CopaibaConexao.transferir( consumidor, id, arquivo1, null, null, null );
		Assert.assertFalse( consumidor.isAberto() );
		Assert.assertEquals( arquivo1.length(), copaiba.executar( "arquivo.length()" ) );
		
		File arquivo2 = File.createTempFile( "copaiba.", ".tmp", tmpDir );
		id = (UUID) copaiba.executar( "Groovy", "copaiba_servico.registrarTransferencia( arquivo, null, null )", null );
		Assert.assertNotNull( id );
		consumidor = novoConsumidor();
		CopaibaConexao.transferir( consumidor, id, arquivo2, null, null, null );
		Assert.assertFalse( consumidor.isAberto() );
		Assert.assertEquals( copaiba.executar( "arquivo.length()" ), arquivo2.length() );
		
		FileInputStream fis1 = new FileInputStream( arquivo1 );
		FileInputStream fis2 = new FileInputStream( arquivo2 );
		int b;
		while( ( b = fis1.read() ) != -1 ){
			if( b != fis2.read() ) Assert.fail();
		}
		fis1.close();
		fis2.close();
		
		verificarFechamento( copaiba );
		
	}
	
	@Test
	public void testarConcorrenciaServidor() throws CopaibaException {
		
		TesteServidoresFornecedor.compartilhado.setContador( 0 );
		
		Thread[] t = new Thread[50];
		
		for( int i = 0; i < t.length; i++ ){
			t[i] = new Thread() {
				@Override
				public void run() {
					try{
						CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );
						for( int i = 0; i < 20; i++ ){
							long valor = (long)( Math.random() * Long.MAX_VALUE );
							Assert.assertEquals(
								valor,
								copaiba.executar( "compartilhado.setValor(" + valor + ")" )
							);
						}
						copaiba.executar( "compartilhado.incrementarContador()" );
						copaiba.fechar( false );
					}catch( Exception e ){
						excecaoIntraThread = e;
					}
				}
			};
		}
		
		for( int i = 0; i < t.length; i++ ){
			t[i].start();
		}
		
		try{
			for( Thread thread : t ) thread.join();
		}catch( Exception e ){
			Assert.fail();
		}
		
		Assert.assertEquals( t.length, TesteServidoresFornecedor.compartilhado.getContador() );
		
	}
	
	@Test
	public void testarConcorrenciaCliente() throws CopaibaException {
		
		final CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );
		copaiba.atribuir( "soma", 0 );
		
		Thread[] t = new Thread[50];
		
		for( int i = 0; i < t.length; i++ ){
			t[i] = new Thread(){
				@Override
				public void run() {
					try{
						for( int n = 0; n < 100; n++ ){
							copaiba.executar( "for( i in 1..100 ) soma++;" );
						}
					}catch( CopaibaException e ){
						excecaoIntraThread = e;
					}
				}	
			};
		}
		
		for( int i = 0; i < t.length; i++ ){
			t[i].start();
		}
		
		try{
			for( Thread thread : t ) thread.join();
		}catch( Exception e ){
			Assert.fail();
		}
		
		Number soma = (Number) copaiba.obter( "soma" );
		Assert.assertEquals( t.length * 100 * 100, soma.intValue() );
		
		verificarFechamento( copaiba );
		
	}
	
	@Test
	public void testarSeguranca() throws CopaibaException {
		
		CopaibaConexao copaiba = novaCopaibaConexao( "jose", "1234" );
		try{
			copaiba.executar( "System.setSecurityManager( null );" );
			Assert.fail();
		}catch( SecurityException e ){
		}
		try{
			copaiba.executar( "java.security.Policy.setPolicy( null );" );
			Assert.fail();
		}catch( SecurityException e ){
		}
		try{
			copaiba.executar( "System.exit( 0 );" );
			Assert.fail();
		}catch( SecurityException e ){
		}
		verificarFechamento( copaiba );
		
		copaiba = novaCopaibaConexao( "jose", "1234" );
		verificarAcesso( copaiba, "compartilhado.restricaoRecurso1()", true  );
		verificarAcesso( copaiba, "compartilhado.restricaoRecurso2()", false );
		verificarAcesso( copaiba, "compartilhado.restricaoRecurso3()", true  );
		verificarAcesso( copaiba, "compartilhado.restricaoAdmin1()"  , true  );
		verificarAcesso( copaiba, "compartilhado.restricaoAdmin2()"  , true  );
		verificarAcesso( copaiba, "compartilhado.restricaoGrupo1()"  , true  );
		verificarAcesso( copaiba, "compartilhado.restricaoGrupo2()"  , false );
		verificarFechamento( copaiba );
		
		copaiba = novaCopaibaConexao( "maria", "4321" );
		verificarAcesso( copaiba, "compartilhado.restricaoRecurso1()", false );
		verificarAcesso( copaiba, "compartilhado.restricaoRecurso2()", true  );
		verificarAcesso( copaiba, "compartilhado.restricaoRecurso3()", true  );
		verificarAcesso( copaiba, "compartilhado.restricaoAdmin1()"  , false );
		verificarAcesso( copaiba, "compartilhado.restricaoAdmin2()"  , false );
		verificarAcesso( copaiba, "compartilhado.restricaoGrupo1()"  , false );
		verificarAcesso( copaiba, "compartilhado.restricaoGrupo2()"  , true  );
		verificarFechamento( copaiba );
		
		copaiba = novaCopaibaConexao( "jose", "1234" );
		copaiba.executar( "gerencia.setPermitirRotina( false )" );
		try{
			copaiba.executar( "gerencia.setPermitirRotina( true )" );
			Assert.fail();
		}catch( SecurityException e ){
		}
		verificarFechamento( copaiba );
		
		copaiba = novaCopaibaConexao( "maria", "4321" );
		try{
			copaiba.executar( "Groovy", "copaiba_servico.registrarTransferencia( new File('teste.txt'), null, null )", null );
			Assert.fail();
		}catch( SecurityException e ){
		}
		verificarFechamento( copaiba );
		
	}
	
	@Test
	public void verificarExcecaoIntraThread() {
		if( excecaoIntraThread != null ){
			Assert.fail( excecaoIntraThread.getMessage() );
		}
	}
	
	private void verificarFechamento( CopaibaConexao copaiba ) throws CopaibaException {
		copaiba.fechar( false );
		Assert.assertFalse( copaiba.isAberta() );
	}
	
	private void verificarAcesso( CopaibaConexao copaiba, String recurso, boolean permitido ) throws CopaibaException {
		if( permitido ){
			copaiba.executar( recurso );
		}else{
			try{
				copaiba.executar( recurso );
				Assert.fail();
			}catch( SecurityException e ){
			}
		}
	}
	
}

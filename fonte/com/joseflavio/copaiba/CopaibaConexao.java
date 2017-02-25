
/*
 *  Copyright (C) 2016 José Flávio de Souza Dias Júnior
 *  
 *  This file is part of Copaíba - <http://www.joseflavio.com/copaiba/>.
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
 *  Direitos Autorais Reservados (C) 2016 José Flávio de Souza Dias Júnior
 * 
 *  Este arquivo é parte de Copaíba - <http://www.joseflavio.com/copaiba/>.
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

import com.joseflavio.urucum.comunicacao.Consumidor;
import com.joseflavio.urucum.comunicacao.Notificacao;
import com.joseflavio.urucum.comunicacao.Servidor;
import com.joseflavio.urucum.comunicacao.SocketConsumidor;

import javax.script.ScriptEngineManager;
import java.io.*;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Conexão à {@link Copaiba}.
 * @author José Flávio de Souza Dias Júnior
 */
public class CopaibaConexao implements Closeable {
	
	private Consumidor consumidor;
	
	private Entrada entrada;
	
	private Saida saida;
	
	private Modo modo;
	
	private String usuario;
	
	/**
	 * Conexão a uma {@link Copaiba}.
	 * @param consumidor Meio de comunicação com o {@link Servidor} {@link Copaiba}.
	 * @param usuario Veja {@link Autenticador#autenticar(String, String)}
	 * @param senha Veja {@link Autenticador#autenticar(String, String)}
	 */
	public CopaibaConexao( Consumidor consumidor, Modo modo, String usuario, String senha ) throws RuntimeException, CopaibaException {
		
		try{
			
			this.consumidor = consumidor;
			this.modo       = modo;
			this.usuario    = usuario;
			
			consumidor.setTempoEspera( 3000 );
			
			InputStream  is = this.consumidor.getInputStream();
			OutputStream os = this.consumidor.getOutputStream();
			
			switch( modo ){
				case JAVA:
					os.write( Comando.INICIO_JAVA.getCodigo() );
					os.flush();
					this.entrada = new JavaEntrada( is );
					this.saida   = new JavaSaida  ( os );
					break;
				case JSON:
					os.write( Comando.INICIO_JSON.getCodigo() );
					os.flush();
					this.entrada = new JSONEntrada( is );
					this.saida   = new JSONSaida  ( os );
					break;
			}
			
			String servico = entrada.texto();
			if( servico == null || ! servico.equals( "Copaíba" ) ){
				throw new CopaibaException( Erro.DESCONHECIDO, "Serviço incompatível." );
			}
			
			float versao = entrada.real32();
			if( versao < Copaiba.VERSAO ) throw new IOException( "Versão incompatível." );
			
			saida.real32( Copaiba.VERSAO );
			saida.texto( usuario );
			saida.texto( senha );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}finally{
			try{
				consumidor.setTempoEspera( 0 );
			}catch( IOException e ){
			}
		}
		
	}
	
	/**
	 * {@link #CopaibaConexao(Consumidor, Modo, String, String) Conexão} em {@link Modo#JAVA}.
	 */
	public CopaibaConexao( Consumidor consumidor, String usuario, String senha ) throws CopaibaException {
		this( consumidor, Modo.JAVA, usuario, senha );
	}
	
	/**
	 * {@link CopaibaConexao} baseada em {@link SocketConsumidor}.
	 * @see #CopaibaConexao(Consumidor, Modo, String, String)
	 */
	public CopaibaConexao( String endereco, int porta, boolean segura, boolean ignorarCertificado, Modo modo, String usuario, String senha ) throws CopaibaException {
		this( novoSocketConsumidor( endereco, porta, segura, ignorarCertificado ), modo, usuario, senha );
	}
	
	/**
	 * {@link SocketConsumidor} em {@link Modo#JAVA}.
	 * @see #CopaibaConexao(Consumidor, Modo, String, String)
	 */
	public CopaibaConexao( String endereco, int porta, boolean segura, boolean ignorarCertificado, String usuario, String senha ) throws CopaibaException {
		this( novoSocketConsumidor( endereco, porta, segura, ignorarCertificado ), Modo.JAVA, usuario, senha );
	}
	
	/**
	 * {@link SocketConsumidor} (sem TLS/SSL) em {@link Modo#JAVA}.
	 * @see #CopaibaConexao(Consumidor, Modo, String, String)
	 */
	public CopaibaConexao( String endereco, int porta, String usuario, String senha ) throws CopaibaException {
		this( novoSocketConsumidor( endereco, porta, false, true ), Modo.JAVA, usuario, senha );
	}
	
	/**
	 * Obtém {@link Informacao} sobre um {@link Servidor} {@link Copaiba}.<br>
	 * O {@link Consumidor} será {@link Consumidor#fechar() fechado}.
	 */
	public static Informacao obterInformacao( Consumidor consumidor ) throws RuntimeException, CopaibaException {

		if( consumidor == null ) throw new IllegalArgumentException( "consumidor" );
		
		try{
			
			consumidor.setTempoEspera( 3000 );
			
			InputStream  is = consumidor.getInputStream();
			OutputStream os = consumidor.getOutputStream();
			
			os.write( Comando.INICIO_INFORMACAO.getCodigo() );
			os.flush();
			
			JSONEntrada entrada = new JSONEntrada( is );
			JSONSaida   saida   = new JSONSaida  ( os );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
			String servico = entrada.texto();
			if( servico == null || ! servico.equals( "Copaíba" ) ){
				throw new CopaibaException( Erro.DESCONHECIDO, "Serviço incompatível." );
			}
			
			final float versao = entrada.real32();
			
			final Collection<Certificate> certificados = new HashSet<Certificate>();
			int totalCertificados = entrada.inteiro32();
			if( totalCertificados > 0 ){
				byte[] cert_bytes = new byte[ totalCertificados ];
				entrada.bytes( cert_bytes, 0, cert_bytes.length );
				CertificateFactory cert_fab = CertificateFactory.getInstance( "X.509" );
				for( Certificate c : cert_fab.generateCertificates( new ByteArrayInputStream( cert_bytes ) ) ){
					certificados.add( c );
				}
			}
			
			final Collection<String> linguagens = new HashSet<String>();
			int totalLinguagens = entrada.inteiro32();
			while( totalLinguagens > 0 ){
				linguagens.add( entrada.texto() );
				totalLinguagens--;
			}
			
			try{
				saida.comando( Comando.SUCESSO );
			}catch( Exception e ){
			}
			
			return new Informacao() {
				@Override
				public float getVersao() {
					return versao;
				}
				@Override
				public Collection<Certificate> getCertificados() {
					return certificados;
				}
				@Override
				public Collection<String> getLinguagens() {
					return linguagens;
				}
			};

		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}finally{
			try{
				consumidor.fechar();
			}catch( IOException e ){
			}
		}
		
	}
	
	/**
	 * Efetua uma transferência de {@link File arquivo}, previamente {@link Servico#registrarTransferencia(File, Notificacao, Notificacao) registrado}.<br>
	 * O {@link Consumidor} será {@link Consumidor#fechar() fechado}.
	 * @param consumidor Canal de transferência.
	 * @param registro {@link Servico#registrarTransferencia(File, Notificacao, Notificacao) Registro} de transferência.
	 * @param arquivo {@link File Arquivo} de escrita (download) ou de leitura (upload, se {@link File#length() tamanho} > 0).
	 * @param exito {@link Notificacao} executada após a conclusão efetiva da transferência. Opcional.
	 * @param erro {@link Notificacao} executada após algum impeditivo da transferência. Opcional.
	 * @param progresso {@link Notificacao} que observa o progresso da transferência, entre 0 e 100 %. Opcional.
	 * @see Servico#registrarTransferencia(File, Notificacao, Notificacao)
	 */
	public static void transferir( Consumidor consumidor, UUID registro, File arquivo, Notificacao<File,?> exito, Notificacao<File,Throwable> erro, Notificacao<File,Float> progresso ) {

		if( consumidor == null ) throw new IllegalArgumentException( "consumidor" );
		if( registro   == null ) throw new IllegalArgumentException( "registro" );
		if( arquivo    == null ) throw new IllegalArgumentException( "arquivo" );
		
		TransferenciaInputStream  tis = null;
		TransferenciaOutputStream tos = null;
		
		FileInputStream  fis = null;
		FileOutputStream fos = null;
		
		boolean   positivo = false;
		Throwable negativo = null;
		
		try{
			
			tis = new TransferenciaInputStream( consumidor.getInputStream() );
			tos = new TransferenciaOutputStream( consumidor.getOutputStream() );
			
			long tamanho = arquivo.length();
			long atualizacao = 0;
			
			if( tamanho > 0 ){
				
				tos.write( Comando.INICIO_ARQUIVO_ESCRITA.getCodigo() );
				tos.texto( registro.toString() );
				
				tos.inteiro64( tamanho );
				
				fis = new FileInputStream( arquivo );
				for( int i = 0; i < tamanho; i++ ){
					tos.write( fis.read() );
					if( progresso != null && --atualizacao <= 0 ){
						try{
							progresso.notificar( arquivo, (float) ( i + 1 ) / tamanho );
						}catch( Exception e ){
						}finally{
							atualizacao = tamanho / 100;							
						}
					}
				}
				
				tos.flush();
				tis.logico();
				
			}else{
				
				tos.write( Comando.INICIO_ARQUIVO_LEITURA.getCodigo() );
				tos.texto( registro.toString() );
				tos.flush();
				
				tamanho = tis.inteiro64();
				
				fos = new FileOutputStream( arquivo );
				for( int i = 0; i < tamanho; i++ ){
					fos.write( tis.read() );
					if( progresso != null && --atualizacao <= 0 ){
						try{
							progresso.notificar( arquivo, (float) ( i + 1 ) / tamanho );
						}catch( Exception e ){
						}finally{
							atualizacao = tamanho / 100;							
						}
					}
				}
				
				tos.logico( true );
				tos.flush();
				
			}
			
			try{
				progresso.notificar( arquivo, 100f );
			}catch( Exception e ){
			}
			
			positivo = true;
			
		}catch( Exception e ){
			
			negativo = e;
			
		}finally{
			
			if( fis != null ){
				try{ fis.close(); }catch( Exception e ){}
				fis = null;
			}
			
			if( fos != null ){
				try{ fos.close(); }catch( Exception e ){}
				fos = null;
			}
			
			try{ tis.close(); }catch( Exception e ){}
			tis = null;
			
			try{ tos.close(); }catch( Exception e ){}
			tos = null;
			
			try{ consumidor.fechar(); }catch( Exception e ){}
			
			if( positivo && exito != null ){
				try{ exito.notificar( arquivo, null ); }catch( Exception e ){}
			}
			
			if( negativo != null && erro != null ){
				try{ erro.notificar( arquivo, negativo ); }catch( Exception e ){}
			}
			
		}
		
	}
	
	private static final SocketConsumidor novoSocketConsumidor( String endereco, int porta, boolean segura, boolean ignorarCertificado ) throws CopaibaException {
		try{
			return new SocketConsumidor( endereco, porta, segura, ignorarCertificado );
		}catch( IOException e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
	}
	
	private static void dispararErro( Entrada entrada, Saida saida ) throws CopaibaException {
		
		try{
			
			Erro erro = Erro.getErro( entrada.inteiro32() );
			String classeNome = entrada.texto();
			String mensagem = entrada.texto();
			
			Class<?> classe = null;
			try{
				classe = Class.forName( classeNome );
			}catch( ClassNotFoundException e ){
				throw new CopaibaException( erro, classeNome + ": " + mensagem );
			}
			
			try{
				saida.comando( Comando.SUCESSO );
			}catch( Exception e ){
			}
			
			if( classe == CopaibaException.class ){
				
				throw new CopaibaException( erro, mensagem );
				
			}else{
				
				Throwable origem = null;
				try{
					origem = (Throwable) classe.getConstructor( String.class ).newInstance( mensagem );
				}catch( Exception e ){
					try{
						origem = (Throwable) classe.newInstance();
					}catch( Exception f ){
						throw new CopaibaException( erro, mensagem );
					}
				}
				
				if( RuntimeException.class.isAssignableFrom( classe ) ) throw origem;
				throw new CopaibaException( erro, origem );
				
			}
		
		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Throwable e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * Executa remotamente um conjunto de instruções (rotina).<br>
	 * Variáveis estarão automaticamente disponibilizadas conforme o processo de
	 * {@link Fornecedor#fornecer(Usuario, Map) fornecimento} de objetos.
	 * Os nomes delas estarão num array de {@link String} denominado "copaiba_nomes".<br>
	 * Exemplo de rotina na linguagem Groovy:
	 * <pre>
	 * for( nome in copaiba_nomes ) println nome
	 * </pre>
	 * Variáveis criadas na {@link CopaibaConexao#executar(String, String, java.io.Writer) execução} corrente serão
	 * mantidas para a próxima {@link CopaibaConexao#executar(String, String, java.io.Writer) execução}.<br>
	 * Variáveis automaticamente criadas e mantidas pelo escopo de execução:<br>
	 * <ul>
	 * <li><code>copaiba_versao     : </code>{@link Copaiba#VERSAO}</li>
	 * <li><code>copaiba_usuario    : </code>{@link Usuario} corrente</li>
	 * <li><code>copaiba_nomes      : </code>{@link String}[]</li>
	 * <li><code>copaiba_assistente : </code>{@link Assistente}</li>
	 * <li><code>copaiba_informacao : </code>{@link Informacao}</li>
	 * <li><code>copaiba_servico    : </code>{@link Servico}</li>
	 * </ul>
	 * @param linguagem Linguagem de programação: {@link ScriptEngineManager#getEngineByName(String)}
	 * @param rotina Instruções a serem executadas.
	 * @param impressao Saída textual da rotina (stdout), incluindo erros (stderr). Opcional.
	 * @param json Exige retorno no formato JSON, representando-o numa {@link String}.
	 * @return {@link Serializable Objeto} resultante da rotina.
	 * @see Copaiba#isPermitirRotina()
	 * @throws RuntimeException
	 * @throws CopaibaException
	 */
	public synchronized Serializable executar( String linguagem, String rotina, Writer impressao, boolean json ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		if( linguagem  == null ) throw new IllegalArgumentException( "linguagem" );
		if( rotina     == null ) throw new IllegalArgumentException( "rotina" );
		
		try{
			
			saida.comando( Comando.ROTINA );
			saida.texto( linguagem );
			saida.texto( rotina );
			saida.logico( impressao != null );
			saida.inteiro8( (byte)( json ? 1 : 0 ) );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
			Serializable resultado = (Serializable) entrada.objeto();
			
			if( impressao != null ){
				String impressaoTexto = entrada.texto();
				if( impressaoTexto != null ){
					impressao.write( impressaoTexto );
					impressao.flush();
				}
			}else{
				entrada.saltarTexto();
			}
			
			return resultado;

		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * {@link #executar(String, String, Writer, boolean) Execução} normal, sem exigir retorno
	 * no formato JSON.
	 */
	public synchronized Serializable executar( String linguagem, String rotina, Writer impressao ) throws RuntimeException, CopaibaException {
		return executar( linguagem, rotina, impressao, false );
	}
	
	/**
	 * O mesmo que {@link #executar(String, String, Writer)}, porém especificando a rotina
	 * através de {@link Reader}.<br>
	 * O {@link Reader} não será {@link Reader#close() fechado}.
	 * @see #executar(String, String, Writer)
	 */
	public synchronized Serializable executar( String linguagem, Reader rotina, Writer impressao ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		if( linguagem  == null ) throw new IllegalArgumentException( "linguagem" );
		if( rotina     == null ) throw new IllegalArgumentException( "rotina" );
		
		try{
			
			StringBuilder s = new StringBuilder( 512 );
			
			int c;
			while( ( c = rotina.read() ) != -1 ){
				s.append( (char) c );
			}
			
			return executar( linguagem, s.toString(), impressao );
			
		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * {@link #executar(String, String, Writer) Execução} direcionada para {@link System#out}.
	 */
	public synchronized Serializable executar( String linguagem, String rotina ) throws RuntimeException, CopaibaException {
		return executar( linguagem, rotina, new PrintWriter( System.out ) );
	}
	
	/**
	 * {@link #executar(String, String, Writer) Execução} com Groovy e direcionada para {@link System#out}.
	 */
	public synchronized Serializable executar( String rotina ) throws RuntimeException, CopaibaException {
		return executar( "Groovy", rotina, new PrintWriter( System.out ) );
	}
	
	/**
	 * Executa um {@link Method método} remoto e retorna o seu resultado.
	 * @param objeto Nome do objeto {@link Fornecedor fornecido} ou previamente criado.
	 * @param metodo Nome do {@link Method método} desejado.
	 * @param json Exige retorno no formato JSON, representando-o numa {@link String}.
	 * @param parametros Parâmetros a serem passados para o {@link Method método} remoto.
	 */
	public synchronized Serializable obter( String objeto, String metodo, boolean json, Serializable... parametros ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		if( objeto     == null ) throw new IllegalArgumentException( "objeto" );
		if( metodo     == null ) throw new IllegalArgumentException( "metodo" );
		
		try{
			
			saida.comando( Comando.MENSAGEM );
			saida.texto( objeto );
			saida.texto( metodo );
			saida.inteiro8( (byte)( json ? 1 : 0 ) );
			saida.objeto( parametros );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
			return entrada.objeto();

		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * Obtém o valor de uma variável remota, {@link Fornecedor fornecida} ou criada através
	 * de {@link #atribuir(String, Serializable) atribuição} ou de
	 * {@link #executar(String, String, Writer) rotina}.
	 * @param variavel Nome da variável desejada.
	 * @param json Exige retorno no formato JSON, representando-o numa {@link String}.
	 */
	public synchronized Serializable obter( String variavel, boolean json ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		if( variavel   == null ) throw new IllegalArgumentException( "variavel" );
		
		try{
			
			saida.comando( Comando.VARIAVEL_LEITURA );
			saida.texto( variavel );
			saida.inteiro8( (byte)( json ? 1 : 0 ) );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
			return entrada.objeto();

		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * {@link #obter(String, boolean)} {@link Serializable} Java.
	 */
	public synchronized Serializable obter( String variavel ) throws RuntimeException, CopaibaException {
		return obter( variavel, false );
	}
	
	/**
	 * Atribui um objeto a uma variável.
	 * @param variavel Nome da variável (existente ou não).
	 * @param objeto Conteúdo da variável. Pode ser <code>null</code>.
	 */
	public synchronized void atribuir( String variavel, Serializable objeto ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		if( variavel   == null ) throw new IllegalArgumentException( "variavel" );
		
		try{
			
			saida.comando( Comando.VARIAVEL_ESCRITA );
			saida.texto( variavel );
			saida.inteiro8( (byte) 0 );
			saida.objeto( objeto );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * Atribui um objeto a uma variável, desserializando-o a partir de um JSON.
	 * @param variavel Nome da variável (existente ou não).
	 * @param classe Nome da classe (tipo) do objeto a ser desserializado.
	 * @param json Conteúdo JSON para desserialização (estado do objeto). Pode ser <code>null</code>.
	 */
	public synchronized void atribuir( String variavel, String classe, String json ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		if( variavel   == null ) throw new IllegalArgumentException( "variavel" );
		if( classe     == null ) throw new IllegalArgumentException( "classe" );
		
		try{
			
			saida.comando( Comando.VARIAVEL_ESCRITA );
			saida.texto( variavel );
			saida.inteiro8( (byte) 1 );
			saida.texto( classe );
			saida.texto( json );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * Apaga uma variável previamente criada através de {@link #executar(String, String, Writer) rotina}
	 * ou de {@link #atribuir(String, Serializable) atribuição}.
	 * @param variavel Nome da variável.
	 */
	public synchronized void remover( String variavel ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		if( variavel   == null ) throw new IllegalArgumentException( "variavel" );
		
		try{
			
			saida.comando( Comando.VARIAVEL_REMOCAO );
			saida.texto( variavel );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * Instancia um objeto remoto e executa um de seus {@link Method métodos}.
	 * @param classe {@link Class#getName() Nome} da classe do objeto.
	 * @param estado Estado serializado do objeto, no formato JSON.
	 * @param metodo Nome do {@link Method método} desejado, sem parâmetro.
	 * @return resultado no formato JSON.
	 */
	public synchronized String solicitar( String classe, String estado, String metodo ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		if( classe == null ) throw new IllegalArgumentException( "classe" );
		if( estado == null ) throw new IllegalArgumentException( "estado" );
		if( metodo == null ) throw new IllegalArgumentException( "metodo" );
		
		try{
			
			saida.comando( Comando.SOLICITACAO );
			saida.texto( classe );
			saida.texto( estado );
			saida.texto( metodo );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
			return entrada.texto();

		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	public synchronized Consumidor getConsumidor() {
		return consumidor;
	}
	
	public synchronized Modo getModo() {
		return modo;
	}
	
	/**
	 * @see CopaibaConexao#CopaibaConexao(Consumidor, Modo, String, String)
	 */
	public synchronized String getUsuario() {
		return usuario;
	}
	
	/**
	 * A {@link CopaibaConexao} está aberta?<br>
	 * Verifica se o {@link Consumidor} está {@link Consumidor#isAberto() aberto}.
	 */
	public synchronized boolean isAberta() {
		return consumidor != null && consumidor.isAberto();
	}
	
	/**
	 * Verifica se a {@link CopaibaConexao} está {@link #isAberta() aberta} e funcionando devidamente,
	 * enviando um {@link Comando#VERIFICACAO}.
	 */
	public synchronized void verificar() throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		
		try{
			
			saida.comando( Comando.VERIFICACAO );
			
			Comando comando = entrada.comando();
			
			if( comando == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else if( comando != Comando.SUCESSO ){
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
		
	}
	
	/**
	 * Envia o {@link Comando#FIM} para a {@link Copaiba} e, se obter {@link Comando#SUCESSO},
	 * {@link Consumidor#fechar() fecha} o {@link Consumidor} e inutiliza esta {@link CopaibaConexao}.
	 * @param garantir Fechar mesmo com negação ou erro da {@link Copaiba}?
	 * @throws CopaibaException caso ocorra algum erro durante o fechamento não forçado.
	 * @see Consumidor#fechar()
	 */
	public synchronized void fechar( boolean garantir ) throws RuntimeException, CopaibaException {
		
		if( consumidor == null ) return;
		
		try{
			
			saida.comando( Comando.FIM );
			Comando resposta = entrada.comando();
			
			if( resposta == Comando.SUCESSO ){
				try{
					consumidor.fechar();
				}catch( Exception e ){
				}finally{
					consumidor = null;
				}
			}else if( resposta == Comando.ERRO ){
				dispararErro( entrada, saida );
			}else{
				throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando desconhecido." );
			}
			
		}catch( CopaibaException e ){
			if( ! garantir ) throw e;
		}catch( Exception e ){
			if( ! garantir ) throw new CopaibaException( Erro.DESCONHECIDO, e );
		}finally{
			
			if( garantir && consumidor != null && consumidor.isAberto() ){
				try{
					consumidor.fechar();
				}catch( Exception e ){
				}finally{
					consumidor = null;
				}
			}
			
			if( consumidor == null ){
				entrada = null;
				saida   = null;
			}
			
		}
		
	}
	
	@Override
	public void close() throws IOException {
		try{
			fechar( true );
		}catch( Exception e ){
			throw new IOException( e );
		}
	}
	
}

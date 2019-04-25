
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

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManagerFactory;
import javax.script.ScriptEngineManager;

import com.joseflavio.urucum.comunicacao.ComunicacaoUtil;
import com.joseflavio.urucum.comunicacao.Consumidor;
import com.joseflavio.urucum.comunicacao.Notificacao;
import com.joseflavio.urucum.comunicacao.Servidor;
import com.joseflavio.urucum.comunicacao.SocketConsumidor;
import com.joseflavio.urucum.json.JSON;

import org.json.JSONException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Conexão à {@link Copaiba}.
 * @author José Flávio de Souza Dias Júnior
 */
public class CopaibaConexao implements Closeable {

	private String endereco;
	
	private int porta;
	
	private boolean segura;
	
	private boolean ignorarCertificado;

	private boolean expressa = false;
	
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
		construir( consumidor, modo, usuario, senha );
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
	 * Estabelece uma conexão normal ou configura uma conexão expressa para uma {@link Copaiba}.
	 * Sendo normal, será utilizado um {@link SocketConsumidor} em {@link Modo#JAVA}.
	 */
	public CopaibaConexao( String endereco, int porta, boolean segura, boolean ignorarCertificado, boolean expressa ) throws CopaibaException {
		if( expressa ){
			this.endereco = endereco;
			this.porta = porta;
			this.segura = segura;
			this.ignorarCertificado = ignorarCertificado;
			this.expressa = expressa;
		}else{
			construir( novoSocketConsumidor( endereco, porta, segura, ignorarCertificado ), Modo.JAVA, "", "" );
		}
	}

	private void construir( Consumidor consumidor, Modo modo, String usuario, String senha ) throws RuntimeException, CopaibaException {
		
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
	 * @param arquivo {@link File Arquivo} de escrita (download) ou de leitura (upload, se {@link File#length() tamanho} &gt; 0).
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

			Erro   erro       = Erro.getErro( entrada.inteiro32() );
			String classeNome = entrada.texto();
			String mensagem   = entrada.texto();

			dispararErro(
				erro,
				classeNome,
				mensagem,
				() -> {
					try{
						saida.comando( Comando.SUCESSO );
					}catch( Exception e ){
					}
				}
			);
			
		}catch( IOException e ) {
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}

	}

	/**
	 * Reconhece e dispara uma exceção conforme seu {@link Class#getName() endereço} e {@link Throwable#getMessage() mensagem}.
	 * @param erro Especificação da exceção no contexto da {@link Copaiba}.
	 * @param classeNome Endereço completo da classe de {@link Throwable exceção}.
	 * @param mensagem Mensagem de erro. Veja {@link Throwable#getMessage()}.
	 * @param sucesso Atividade a ser executada quando do reconhecimento positivo da exceção a ser disparada. Pode ser null.
	 * @throws RuntimeException Classe de exceção que será disparada preferencialmente.
	 * @throws CopaibaException Classe de exceção secundária, normalmente disparada encapsulando outra exceção.
	 */
	private static void dispararErro( Erro erro, String classeNome, String mensagem, Runnable sucesso ) throws RuntimeException, CopaibaException {
		
		if( mensagem == null ) mensagem = "";

		try{
			
			Class<?> classe = null;
			try{
				classe = Class.forName( classeNome );
			}catch( ClassNotFoundException e ){
				throw new CopaibaException( erro, classeNome + ": " + mensagem );
			}

			if( sucesso != null ){
				sucesso.run();
			}
			
			if( classe == CopaibaException.class ){
				
				throw new CopaibaException( erro, mensagem );
				
			}else{
				
				Throwable causa = null;
				try{
					causa = (Throwable) classe.getConstructor( String.class ).newInstance( mensagem );
				}catch( Exception e ){
					try{
						causa = (Throwable) classe.getConstructor().newInstance();
					}catch( Exception f ){
						throw new CopaibaException( erro, classeNome + ": " + mensagem );
					}
				}
				
				if( RuntimeException.class.isAssignableFrom( classe ) ) throw (RuntimeException) causa;
				else throw new CopaibaException( erro, causa );
				
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
		
		if( linguagem  == null ) throw new IllegalArgumentException( "linguagem" );
		if( rotina     == null ) throw new IllegalArgumentException( "rotina" );

		if( expressa ){
			throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando indisponível através de conexão expressa." );
		}
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );

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
		
		if( linguagem  == null ) throw new IllegalArgumentException( "linguagem" );
		if( rotina     == null ) throw new IllegalArgumentException( "rotina" );
		
		if( expressa ){
			throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando indisponível através de conexão expressa." );
		}
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		
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
		
		if( objeto     == null ) throw new IllegalArgumentException( "objeto" );
		if( metodo     == null ) throw new IllegalArgumentException( "metodo" );
		
		if( expressa ){
			throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando indisponível através de conexão expressa." );
		}
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		
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
		
		if( variavel   == null ) throw new IllegalArgumentException( "variavel" );
		
		if( expressa ){
			throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando indisponível através de conexão expressa." );
		}

		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );

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
		
		if( variavel   == null ) throw new IllegalArgumentException( "variavel" );
		
		if( expressa ){
			throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando indisponível através de conexão expressa." );
		}

		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		
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
		
		if( variavel   == null ) throw new IllegalArgumentException( "variavel" );
		if( classe     == null ) throw new IllegalArgumentException( "classe" );
		
		if( expressa ){
			throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando indisponível através de conexão expressa." );
		}
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		
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
		
		if( variavel   == null ) throw new IllegalArgumentException( "variavel" );
		
		if( expressa ){
			throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando indisponível através de conexão expressa." );
		}
		
		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		
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
		
		if( classe == null ) throw new IllegalArgumentException( "classe" );
		if( estado == null ) throw new IllegalArgumentException( "estado" );
		if( metodo == null ) throw new IllegalArgumentException( "metodo" );

		if( expressa ){
			return conectarExpressa( "SOLICITAR", classe, metodo, estado );
		}

		if( consumidor == null ) throw new CopaibaException( Erro.CONEXAO_FECHADA, "Conexão fechada." );
		
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
	 * Verifica se o {@link Consumidor} está {@link Consumidor#isAberto() aberto}.<br>
	 * Se a conexão for do tipo expressa, sempre retornará false.
	 */
	public synchronized boolean isAberta() {
		return consumidor != null && consumidor.isAberto();
	}
	
	/**
	 * Verifica se a {@link CopaibaConexao} está {@link #isAberta() aberta} e funcionando devidamente,
	 * enviando um {@link Comando#VERIFICACAO}.
	 */
	public synchronized void verificar() throws RuntimeException, CopaibaException {
		
		if( expressa ){
			throw new CopaibaException( Erro.COMANDO_DESCONHECIDO, "Comando indisponível através de conexão expressa." );
		}

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
	 * {@link Consumidor#fechar() fecha} o {@link Consumidor} e inutiliza esta {@link CopaibaConexao}.<br>
	 * Se a conexão for do tipo expressa, este método nada fará.
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
			
		}catch( RuntimeException e ){
			if( ! garantir ) throw e;
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

	/**
	 * Efetiva uma conexão expressa enviando a cadeia de comando desejado e
	 * retornando a resposta final.
	 */
	private String conectarExpressa( String... comando ) throws RuntimeException, CopaibaException {

		NioEventLoopGroup grupo = new NioEventLoopGroup( 1 );

		try{

			final TrustManagerFactory tmf =
				ignorarCertificado ?
				InsecureTrustManagerFactory.INSTANCE :
				ComunicacaoUtil.iniciarTrustManagerFactory();

			final SslContext ssl =
				segura ?
				SslContextBuilder.forClient().trustManager( tmf ).build() :
				null;

			Bootstrap servico = new Bootstrap();

			ConexaoExpressa conexaoExpressa = new ConexaoExpressa( comando );

			servico.group( grupo )
				.channel( NioSocketChannel.class )
				.option( ChannelOption.SO_KEEPALIVE, true )
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel( SocketChannel ch ) throws Exception {

						ChannelPipeline pipeline = ch.pipeline();

						if( ssl != null ){
							pipeline.addLast( ssl.newHandler( ch.alloc(), endereco, porta ) );
						}

						pipeline.addLast( conexaoExpressa );

					}
				});
			
			servico.connect( endereco, porta ).sync().channel().closeFuture().sync();

			return conexaoExpressa.esperarResposta();

		}catch( RuntimeException e ){
			throw e;
		}catch( CopaibaException e ){
			throw e;
		}catch( Exception e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}finally{

			grupo.shutdownGracefully( 0, 0, TimeUnit.MILLISECONDS );

		}

	}

	/**
	 * Conexão expressa com um servidor {@link Copaiba},
	 * compreendendo um ciclo completo de vida,
	 * conexão-requisição-resposta-fechamento.
	 */
	private final class ConexaoExpressa extends ChannelInboundHandlerAdapter {

		private String[] comando;
		private ByteBuf entrada, saida;
		private Charset codificacao;
		private JSON erro;
		private CompletableFuture<String> conclusao = new CompletableFuture<>();

		public ConexaoExpressa( String... comando ) {
			this.comando = comando;
		}

		@Override
		public void handlerAdded( ChannelHandlerContext ctx ) {
			entrada     = ctx.alloc().buffer( 150 );
			saida       = ctx.alloc().buffer( 150 );
			codificacao = Charset.forName( "UTF-8" );
		}

		@Override
		public void handlerRemoved( ChannelHandlerContext ctx ) {
			
			if( entrada.refCnt() > 0 ) entrada.release( entrada.refCnt() );
			if( saida  .refCnt() > 0 ) saida  .release( saida  .refCnt() );
			
			comando     = null;
			entrada     = null;
			saida       = null;
			codificacao = null;

		}

		@Override
		public void channelActive( ChannelHandlerContext ctx ) throws Exception {

			final int total  = comando.length;
			final int ultimo = total - 1;

			for( int i = 0; i < total; i++ ){
				saida.writeCharSequence( comando[i], codificacao );
				if( i != ultimo ) saida.writeByte( '\n' );
			}

			saida.writeBytes( Copaiba.MARCACAO_FIM );

			ctx.writeAndFlush( saida );

		}

		@Override
		public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

			try{

				if( erro == null && entrada.readableBytes() == 0 ){
					exceptionCaught( ctx, new IOException( "0 bytes" ) );
					conclusao.complete( null );
					return;
				}
	
				String conteudo = entrada.toString( codificacao );
				String resposta = null;
	
				if( conteudo.contains( "_copaiba_erro_codigo" ) ){
					try{
						JSON json = new JSON( conteudo );
						if( json.has( "_copaiba_erro_codigo" ) ) erro = json;
						else resposta = conteudo;
					}catch( JSONException e ){
						resposta = conteudo;	
					}
				}else{
					resposta = conteudo;
				}
	
				conclusao.complete( resposta );

			}catch( Exception e ){

				exceptionCaught( ctx, e );
				conclusao.complete( null );

			}
			
		}

		@Override
		public void channelRead( ChannelHandlerContext ctx, Object msg ) {
			ByteBuf bb = (ByteBuf) msg;
			entrada.writeBytes( bb );
			bb.release();
		}

		@Override
		public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) {
			
			Erro motivo =
				cause instanceof CopaibaException ?
				((CopaibaException)cause).getErro() :
				Erro.DESCONHECIDO;
			
			if( motivo == null ) motivo = Erro.DESCONHECIDO;

			erro = new JSON()
				.put( "exito", false )
				.put( "codigo", motivo.getCodigo() )
				.put( "_copaiba_erro_codigo", motivo.getCodigo() )
				.put( "_copaiba_erro_nome", motivo.toString() )
				.put( "_copaiba_erro_classe", cause.getClass().getName() )
				.put( "_copaiba_erro_mensagem", cause.getMessage() );

			ctx.close();

		}

		public String esperarResposta() throws CopaibaException {
			
			String resposta = null;

			try{
				resposta = conclusao.get();
			}catch( Exception e ){
				throw new CopaibaException( Erro.DESCONHECIDO, e );
			}

			if( erro != null ){
				dispararErro(
					Erro.getErro( erro.getInt( "_copaiba_erro_codigo" ) ),
					erro.optString( "_copaiba_erro_classe", IOException.class.getName() ),
					erro.optString( "_copaiba_erro_mensagem", erro.optString( "_copaiba_erro_nome" ) ),
					null
				);
			}
			
			return resposta;
			
		}

	}

}

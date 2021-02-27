
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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseflavio.copaiba.util.GroovyApenasAuditor;
import com.joseflavio.copaiba.util.SimplesAutenticador;
import com.joseflavio.copaiba.util.SimplesFornecedor;
import com.joseflavio.copaiba.util.TempoLimiteTransformador;
import com.joseflavio.urucum.comunicacao.ComunicacaoUtil;
import com.joseflavio.urucum.comunicacao.Consumidor;
import com.joseflavio.urucum.comunicacao.Notificacao;
import com.joseflavio.urucum.comunicacao.Servidor;
import com.joseflavio.urucum.comunicacao.SocketServidor;
import com.joseflavio.urucum.json.JSON;
import com.joseflavio.urucum.json.JSONUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Copaíba, do Tupi kupa'iwa.<br>
 * Programação em tempo de execução remota.
 * @author José Flávio de Souza Dias Júnior
 * @see Autenticador
 * @see Auditor
 * @see Fornecedor
 */
public class Copaiba implements Closeable {
	
	/**
	 * Versão desta {@link Copaiba}.
	 */
	public static final float VERSAO = 1.0f;
	
	/**
	 * Caracteres que indicam fim de mensagem.
	 */
	public static final byte[] MARCACAO_FIM = { '|', '~', '\r', '\n' };

	private static Map<Thread,Sessao> sessoes;
	
	private ScriptEngineManager gerenciador;
	
	private HashSet<String> linguagens;

	private Map<String,ScriptEngine> processadores;
	
	private HashSet<Certificate> certificados;
	
	private Autenticador autenticador;
	
	private Transformador transformador;
	
	private Auditor auditor;
	
	private Fornecedor fornecedor;
	
	private Assistente assistente;
	
	private Servidor servidor;

	private EventLoopGroup coordenadores;

	private EventLoopGroup colaboradores;
	
	private Map<UUID,Transferencia> transferencia;
	
	private ScheduledFuture<?> transferenciaManutencao;
	
	private boolean permitirRotina        = true;
	private boolean permitirMensagem      = true;
	private boolean permitirLeitura       = true;
	private boolean permitirAtribuicao    = true;
	private boolean permitirRemocao       = true;
	private boolean permitirSolicitacao   = true;
	private boolean permitirTransferencia = true;
	
	private boolean publicarCertificados = true;
	
	private CopaibaRecepcao copaibaRecepcao;

	/**
	 * Instancia uma nova {@link Copaiba}.
	 * @param autenticador {@link Autenticador}, opcional. Exemplo: {@link SimplesAutenticador}.
	 * @param transformador {@link Transformador}, opcional. Exemplo: {@link TempoLimiteTransformador}.
	 * @param auditor {@link Auditor}, opcional. Exemplo: {@link GroovyApenasAuditor}.
	 * @param fornecedor {@link Fornecedor}, opcional. Exemplo: {@link SimplesFornecedor}.
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.criar"</code>
	 * @see #abrir(int, boolean, boolean)
	 */
	public Copaiba( Autenticador autenticador, Transformador transformador, Auditor auditor, Fornecedor fornecedor ) {
		
		seguranca( "Copaiba.criar" );
		
		this.autenticador  = autenticador;
		this.transformador = transformador;
		this.auditor       = auditor;
		this.fornecedor    = fornecedor;

	}
	
	/**
	 * {@link Copaiba} inicialmente sem {@link Autenticador}, {@link Transformador} e {@link Auditor}.
	 * @see SimplesFornecedor
	 * @see #abrir(int, boolean, boolean)
	 * @see #Copaiba(Autenticador, Transformador, Auditor, Fornecedor)
	 */
	public Copaiba( Fornecedor fornecedor ) {
		this( null, null, null, fornecedor );
	}
	
	/**
	 * {@link Copaiba} inicialmente sem {@link Autenticador}, {@link Transformador}, {@link Auditor} e {@link Fornecedor}.
	 * @see #abrir(int, boolean, boolean)
	 * @see #Copaiba(Autenticador, Transformador, Auditor, Fornecedor)
	 */
	public Copaiba() {
		this( null, null, null, null );
	}

	/**
	 * Ativa a recepção de {@link CopaibaConexao conexões}. Aconselha-se,
	 * antes de chamar este método, proteger as classes e objetos públicos
	 * através de {@link SecurityManager}. Para mais detalhes, veja {@link Fornecedor}.
	 * @param porta Porta de comunicação por {@link java.net.Socket}.
	 * @param segura Conexão segura? Proteção através do protocolo TLS/SSL. Veja {@link ComunicacaoUtil#abrirKeyStore()}.
	 * @param expressa Modo de comunicação expressa? Veja {@link #isExpressa()}.
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.abrir"</code>
	 * @see Autenticador
	 * @see Auditor
	 * @see Fornecedor
	 * @see #isExpressa()
	 * @see #isAberta()
	 * @see #fechar()
	 */
	public final void abrir( int porta, boolean segura, boolean expressa ) throws CopaibaException {
		if( expressa ){
			abrirExpressa( porta, segura );
		}else{
			abrir( porta, segura );
		}
	}

	/**
	 * {@link Copaiba} completa disponível através de {@link Servidor}.
	 * @see #abrir(int, boolean, boolean)
	 */
	public final void abrir( Servidor servidor ) throws CopaibaException {
		
		seguranca( "Copaiba.abrir" );

		if( isAberta() ) throw new CopaibaException( Erro.ESTADO_INVALIDO, "A Copaíba já está aberta." );
		
		if( servidor == null || ! servidor.isAberto() ) throw new IllegalArgumentException( "O servidor deve estar aberto." );
		
		sessoes = Collections.synchronizedMap( new HashMap<Thread,Sessao>() );

		this.servidor      = servidor;
		this.gerenciador   = new ScriptEngineManager();
		this.linguagens    = new HashSet<String>();
		this.processadores = new HashMap<String,ScriptEngine>();
		this.certificados  = new HashSet<Certificate>();
		this.transferencia = Collections.synchronizedMap( new HashMap<UUID,Transferencia>( 128 ) );
		
		for( ScriptEngineFactory sef : this.gerenciador.getEngineFactories() ){
			this.linguagens.add( sef.getLanguageName() );
		}
		
		try{
			KeyStore ks = ComunicacaoUtil.abrirKeyStore();
			Enumeration<String> ks_nomes = ks.aliases();
			while( ks_nomes.hasMoreElements() ){
				this.certificados.add( ks.getCertificate( ks_nomes.nextElement() ) );
			}
		}catch( Exception e ){
		}

		if( transferenciaManutencao == null || transferenciaManutencao.isDone() ){
			transferenciaManutencao = new ScheduledThreadPoolExecutor( 1 ).scheduleWithFixedDelay( new Runnable() {
				@Override
				public void run() {
					try{
						if( transferencia == null ) return;
						long agora = System.currentTimeMillis();
						long tempoMaximo = 30 * 60 * 1000; //30 min
						Iterator<Transferencia> transf = transferencia.values().iterator();
						while( transf.hasNext() ){
							if( ( agora - transf.next().criacao ) >= tempoMaximo ){
								transf.remove();
							}
						}
					}catch( Exception e ){
					}
				}
			}, 10, 10, TimeUnit.MINUTES );
		}

		if( copaibaRecepcao != null ){
			copaibaRecepcao.pronta( this );
		}
		
		while( true ){
			if( this.servidor == null ) break;
			try{
				new Sessao( this.servidor.aceitar() ).iniciar();
			}catch( Exception e ){
				try{
					Thread.sleep( 100 );
				}catch( InterruptedException ie ){
					throw new CopaibaException( Erro.ESTADO_INVALIDO, ie );
				}
			}
		}
		
	}
	
	/**
	 * {@link Copaiba} completa disponível através de {@link SocketServidor}.
	 * @see #abrir(Servidor)
	 * @see #abrir(int, boolean, boolean)
	 */
	public final void abrir( int porta, boolean segura ) throws CopaibaException {
		try{
			abrir( new SocketServidor( porta, segura ) );
		}catch( IOException e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
	}
	
	/**
	 * {@link Copaiba} completa disponível através de {@link SocketServidor},
	 * porta 8884 e segurança TLS/SSL desativada.
	 * @see #abrir(Servidor)
	 * @see #abrir(int, boolean, boolean)
	 */
	public final void abrir() throws CopaibaException {
		try{
			abrir( new SocketServidor( 8884, false ) );
		}catch( IOException e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
	}

	private void abrirExpressa( int porta, boolean segura ) throws CopaibaException {

		seguranca( "Copaiba.abrir" );

		if( isAberta() ) throw new CopaibaException( Erro.ESTADO_INVALIDO, "A Copaíba já está aberta." );
		
		try{
			
			coordenadores = new NioEventLoopGroup( 1 );
			colaboradores = new NioEventLoopGroup();

			final SslContext ssl =
				segura ?
				SslContextBuilder.forServer( ComunicacaoUtil.iniciarKeyManagerFactory() ).build() :
				null;

			ServerBootstrap servico = new ServerBootstrap();

			servico.group( coordenadores, colaboradores )
				.channel( NioServerSocketChannel.class )
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel( SocketChannel ch ) throws Exception {

						ChannelPipeline pipeline = ch.pipeline();
						
						pipeline.addLast(
							new IdleStateHandler(
								false,
								0,
								SessaoExpressa.TEMPO_MAXIMO,
								0,
								TimeUnit.MILLISECONDS
							)
						);

						if( ssl != null ){
							pipeline.addLast( ssl.newHandler( ch.alloc() ) );
						}

						pipeline.addLast( new SessaoExpressa() );

					}
				})
				.option( ChannelOption.SO_BACKLOG, 128 )
				.childOption( ChannelOption.SO_KEEPALIVE, true );

			if( copaibaRecepcao != null ){
				copaibaRecepcao.pronta( this );
			}

			servico.bind( porta ).sync().channel().closeFuture().sync();

		}catch( Exception e ){
			if( e instanceof CopaibaException ) throw (CopaibaException) e;
			else if( e instanceof InterruptedException ) throw new CopaibaException( Erro.ESTADO_INVALIDO, e );
			else throw new CopaibaException( Erro.DESCONHECIDO, e );
		}

	}
	
	/**
	 * A {@link Copaiba} está aberta a conexões de clientes?<br>
	 * Este método não verifica a efetividade da conexão do servidor.
	 * @see #abrir(int, boolean, boolean)
	 * @see #abrir(Servidor)
	 * @see #fechar()
	 */
	public final boolean isAberta() {
		return coordenadores != null || servidor != null;
	}

	/**
	 * Esta {@link Copaiba} está no modo de comunicação expressa?
	 * A {@link Copaiba} expressa fornece um protocolo assíncrono de comunicação,
	 * sem manutenção de sessão, com foco em ações simples e rápidas,
	 * o que impossibilita a utilização de vários recursos da {@link Copaiba}.
	 * Por enquanto, o modo de comunicação expressa suporta apenas a
	 * {@link CopaibaConexao#solicitar(String, String, String) solicitação}.
	 */
	public final boolean isExpressa() {
		return coordenadores != null;
	}
	
	/**
	 * Fecha este servidor e encerra todas as atividades internas, inclusive
	 * as {@link CopaibaConexao conexões} ativas.<br>
	 * Contudo, poderá {@link #abrir(int, boolean, boolean) abrir} novamente.
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.fechar"</code>
	 * @see #abrir(int, boolean, boolean)
	 * @see #isAberta()
	 */
	public final void fechar() {

		seguranca( "Copaiba.fechar" );
		
		if( sessoes != null ){
			for( Sessao s : sessoes.values() ){
				try{
					s.canal.fechar();
				}catch( Exception e ){
				}
			}
			sessoes.clear();
			sessoes = null;
		}

		if( servidor != null ){
			try{
				servidor.fechar();
			}catch( Exception e ){
			}finally{
				servidor = null;
			}
		}

		if( coordenadores != null ){
			try{
				coordenadores.shutdownGracefully();
			}catch( Exception e ){
			}finally{
				coordenadores = null;
			}
		}

		if( colaboradores != null ){
			try{
				colaboradores.shutdownGracefully();
			}catch( Exception e ){
			}finally{
				colaboradores = null;
			}
		}
		
		if( transferenciaManutencao != null ){
			try{
				if( ! transferenciaManutencao.isDone() ){
					transferenciaManutencao.cancel( true );
				}
			}catch( Exception e ){
			}finally{
				transferenciaManutencao = null;
			}
		}

		
		if( linguagens != null ){
			linguagens.clear();
			linguagens = null;
		}

		if( processadores != null ){
			processadores.clear();
			processadores = null;
		}

		if( certificados != null ){
			certificados.clear();
			certificados = null;
		}

		if( transferencia != null ){
			transferencia.clear();
			transferencia = null;
		}
		
		gerenciador = null;
		
	}
	
	@Override
	public void close() throws IOException {
		fechar();
	}
	
	public Servidor getServidor() {
		return servidor;
	}
	
	public Autenticador getAutenticador() {
		return autenticador;
	}
	
	/**
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setAutenticador( Autenticador autenticador ) {
		seguranca( "Copaiba.conf" );
		this.autenticador = autenticador;
	}
	
	public Transformador getTransformador() {
		return transformador;
	}
	
	/**
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setTransformador( Transformador transformador ) {
		seguranca( "Copaiba.conf" );
		this.transformador = transformador;
	}
	
	public Auditor getAuditor() {
		return auditor;
	}
	
	/**
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setAuditor( Auditor auditor ) {
		seguranca( "Copaiba.conf" );
		this.auditor = auditor;
	}
	
	public Fornecedor getFornecedor() {
		return fornecedor;
	}
	
	/**
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setFornecedor( Fornecedor fornecedor ) {
		seguranca( "Copaiba.conf" );
		this.fornecedor = fornecedor;
	}
	
	public Assistente getAssistente() {
		return assistente;
	}
	
	public void setAssistente( Assistente assistente ) {
		seguranca( "Copaiba.conf" );
		this.assistente = assistente;
	}
	
	/**
	 * @see #setPermitirRotina(boolean)
	 */
	public boolean isPermitirRotina() {
		return permitirRotina;
	}
	
	/**
	 * Permitir {@link CopaibaConexao#executar(String, String, Writer)}?
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setPermitirRotina( boolean permitirRotina ) {
		seguranca( "Copaiba.conf" );
		this.permitirRotina = permitirRotina;
	}
	
	/**
	 * @see #setPermitirMensagem(boolean)
	 */
	public boolean isPermitirMensagem() {
		return permitirMensagem;
	}
	
	/**
	 * Permitir {@link CopaibaConexao#obter(String, String, boolean, Serializable...)}?
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setPermitirMensagem( boolean permitirMensagem ) {
		seguranca( "Copaiba.conf" );
		this.permitirMensagem = permitirMensagem;
	}
	
	/**
	 * @see #setPermitirLeitura(boolean)
	 */
	public boolean isPermitirLeitura() {
		return permitirLeitura;
	}
	
	/**
	 * Permitir {@link CopaibaConexao#obter(String, boolean)}?
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setPermitirLeitura( boolean permitirLeitura ) {
		seguranca( "Copaiba.conf" );
		this.permitirLeitura = permitirLeitura;
	}
	
	/**
	 * @see #setPermitirAtribuicao(boolean)
	 */
	public boolean isPermitirAtribuicao() {
		return permitirAtribuicao;
	}
	
	/**
	 * Permitir {@link CopaibaConexao#atribuir(String, Serializable)}
	 * e {@link CopaibaConexao#atribuir(String, String, String)}?
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setPermitirAtribuicao( boolean permitirAtribuicao ) {
		seguranca( "Copaiba.conf" );
		this.permitirAtribuicao = permitirAtribuicao;
	}
	
	/**
	 * @see #setPermitirRemocao(boolean)
	 */
	public boolean isPermitirRemocao() {
		return permitirRemocao;
	}
	
	/**
	 * Permitir {@link CopaibaConexao#remover(String)}?
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setPermitirRemocao( boolean permitirRemocao ) {
		seguranca( "Copaiba.conf" );
		this.permitirRemocao = permitirRemocao;
	}
	
	/**
	 * @see #setPermitirSolicitacao(boolean)
	 */
	public boolean isPermitirSolicitacao() {
		return permitirSolicitacao;
	}
	
	/**
	 * Permitir {@link CopaibaConexao#solicitar(String, String, String)}?
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setPermitirSolicitacao( boolean permitirSolicitacao ) {
		seguranca( "Copaiba.conf" );
		this.permitirSolicitacao = permitirSolicitacao;
	}
	
	/**
	 * @see #setPermitirTransferencia(boolean)
	 */
	public boolean isPermitirTransferencia() {
		return permitirTransferencia;
	}
	
	/**
	 * Permitir {@link CopaibaConexao#transferir(Consumidor, UUID, File, Notificacao, Notificacao, Notificacao)}?
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setPermitirTransferencia( boolean permitirTransferencia ) {
		seguranca( "Copaiba.conf" );
		this.permitirTransferencia = permitirTransferencia;
	}
	
	/**
	 * @see #setPublicarCertificados(boolean)
	 */
	public boolean isPublicarCertificados() {
		return publicarCertificados;
	}
	
	/**
	 * Publicar {@link Informacao#getCertificados()}?
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setPublicarCertificados( boolean publicarCertificados ) {
		seguranca( "Copaiba.conf" );
		this.publicarCertificados = publicarCertificados;
	}
	
	public CopaibaRecepcao getCopaibaRecepcao() {
		return copaibaRecepcao;
	}
	
	/**
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.conf"</code>
	 */
	public void setCopaibaRecepcao( CopaibaRecepcao copaibaRecepcao ) {
		seguranca( "Copaiba.conf" );
		this.copaibaRecepcao = copaibaRecepcao;
	}
	
	private ScriptEngine getProcessador( String linguagem ) {

		synchronized( processadores ){
		
			ScriptEngine processador = processadores.get( linguagem );
			if( processador != null ) return processador;
			
			processador = gerenciador.getEngineByName( linguagem );
			if( processador == null ) return null;
			
			processadores.put( linguagem, processador );
			return processador;
			
		}
		
	}
	
	/**
	 * {@link Usuario} responsável pela {@link Thread} {@link Thread#currentThread() corrente}.
	 * @return <code>null</code>, caso a {@link Thread} não esteja associada a um {@link Usuario}.
	 */
	public static final Usuario getUsuario() {
		if( sessoes == null ) return null;
		Sessao sessao = sessoes.get( Thread.currentThread() );
		return sessao != null && sessao.usuario != null ? sessao.usuario.clonar() : null;
	}
	
	/**
	 * {@link Autenticador#getGrupos(Usuario)}
	 */
	public static final String[] getGrupos( Usuario usuario ) throws CopaibaException {
		if( usuario == null ) throw new IllegalArgumentException();
		if( usuario.autenticador == null ) return new String[0];
		return usuario.autenticador.getGrupos( usuario );
	}
	
	/**
	 * Verifica se o {@link Usuario} {@link #getUsuario() corrente}
	 * pertence ao <code>grupo</code> indicado, conforme {@link Autenticador#pertence(Usuario, String)}.
	 * @throws SecurityException caso o {@link Usuario} não pertença ao <code>grupo</code>.
	 * @see #getGrupos(Usuario)
	 */
	public static final void usuarioPertence( String grupo ) throws SecurityException {
		
		if( grupo == null || grupo.isEmpty() ) throw new SecurityException( "Grupo inválido." );
		
		Usuario usuario = getUsuario();
		if( usuario == null ) throw new SecurityException( "Usuário não identificado." );
		if( usuario.autenticador == null ) throw new SecurityException( "Usuário não autenticado." );
		
		try{
			if( ! usuario.autenticador.pertence( usuario, grupo ) ){
				throw new SecurityException( "Usuário " + usuario.getNome() + " não pertence ao grupo " + grupo + "." );
			}
		}catch( CopaibaException e ){
			throw new SecurityException( e );
		}
		
	}
	
	/**
	 * Verifica se o {@link Usuario} {@link #getUsuario() corrente} possui
	 * autorização para acessar o <code>recurso</code> indicado,
	 * com base nas {@link Policy#getPolicy() políticas} de {@link System#getSecurityManager() segurança}.<br>
	 * Serão realizadas {@link SecurityManager#checkPermission(java.security.Permission)} com as possíveis
	 * variações de {@link CopaibaPermission}, conforme o {@link Usuario#getNome() nome} e os
	 * {@link #getGrupos(Usuario) grupos} do {@link Usuario}.
	 * @param recurso Nome do recurso para o qual o {@link Usuario} deve possuir autorização.
	 * @throws SecurityException caso o {@link Usuario} não possua autorização.
	 * @see SecurityManager
	 * @see Policy
	 * @see CopaibaPermission
	 */
	public static final void usuarioAutorizado( String recurso ) throws SecurityException {
		
		if( recurso == null || recurso.isEmpty() ) throw new SecurityException( "Recurso inválido." );
		
		Usuario usuario = getUsuario();
		if( usuario == null ) throw new SecurityException( "Usuário não identificado." );
		
		SecurityManager sm = System.getSecurityManager();
		if( sm == null ) return;
		
		try{
			
			sm.checkPermission( new CopaibaPermission( usuario.getNome(), recurso ) );
			
		}catch( SecurityException e ){
			
			try{
				
				for( String grupo : getGrupos( usuario ) ){
					try{
						sm.checkPermission( new CopaibaPermission( "$" + grupo, recurso ) );
						return;
					}catch( SecurityException g ){
					}
				}
				
			}catch( CopaibaException f ){
				throw new SecurityException( f );
			}
			
			throw e;
			
		}
		
	}
	
	private final void seguranca( String recurso ) {
		SecurityManager sm = System.getSecurityManager();
		if( sm != null ) sm.checkPermission( new RuntimePermission( recurso ) );
	}
	
	private class Sessao {
		
		private Consumidor   canal;
		private InputStream  canalE;
		private OutputStream canalS;
		
		private Entrada entrada;
		private Saida   saida;
		
		private TemporariaOutputStream tmp;
		private Saida                  tmpSaida;
		private Writer                 tmpWriter;
		
		private Usuario usuario;
		
		private ScriptContext contexto;
		
		private Bindings escopo;
		
		private List<String> fornecido;
		
		private ObjectMapper conversor;
		
		public Sessao( Consumidor canal ) {
			this.canal = canal;
		}
		
		public void iniciar() {
			
			new Thread(){
				
				@Override
				public void run() {
					
					try{
						
						canal.setTempoEspera( 3000 );
						
						canalE    = canal.getInputStream();
						canalS    = canal.getOutputStream();
						tmp       = new TemporariaOutputStream( 1024 * 5 );
						tmpWriter = new OutputStreamWriter( tmp, "UTF-8" );
						
						Comando comando = Comando.getComando( (byte) canalE.read() );
						
						// --------------------------------------------------------
						
						if( comando == Comando.INICIO_JAVA ){
							
							entrada  = new JavaEntrada( canalE );
							saida    = new JavaSaida( canalS );
							tmpSaida = new JavaSaida( tmp );
							
						}else if( comando == Comando.INICIO_JSON ){
							
							entrada  = new JSONEntrada( canalE );
							saida    = new JSONSaida( canalS );
							tmpSaida = new JSONSaida( tmp );
							
						}else if( comando == Comando.INICIO_INFORMACAO ){
							
							processarInformacao();
							return;
							
						}else if( comando == Comando.INICIO_ARQUIVO_LEITURA ){
							
							processarTransferencia( true );
							return;
							
						}else if( comando == Comando.INICIO_ARQUIVO_ESCRITA ){
							
							processarTransferencia( false );
							return;
							
						}else{
							
							return;
							
						}
						
						saida.texto( "Copaíba" );
						saida.real32( Copaiba.VERSAO );
						
						float  versao = entrada.real32();
						String nome   = entrada.texto();
						String senha  = entrada.texto();
						
						if( autenticador != null && ! autenticador.autenticar( nome, senha ) ){
							enviar( Erro.AUTENTICACAO, CopaibaException.class, "Falha na autenticação." );
							return;
						}else{
							saida.comando( Comando.SUCESSO );
							usuario = new Usuario( autenticador, nome, versao, new Date() );
							sessoes.put( this, Sessao.this );
						}
						
						contexto = new SimpleScriptContext();
						escopo = contexto.getBindings( ScriptContext.ENGINE_SCOPE );
						fornecido = new ArrayList<String>();
							
						while( true ){
							
							canal.setTempoEspera( 0 );
							
							comando = entrada.comando();
							
							canal.setTempoEspera( 3000 );
							
							if( comando == Comando.ROTINA ){
								
								String  linguagem = entrada.texto();
								String  rotina    = entrada.texto();
								boolean imprimir  = entrada.logico();
								boolean json      = entrada.inteiro8() == 1;
								
								if( ! permitirRotina ){
									enviar( Erro.PERMISSAO, SecurityException.class, "Sem permissão para executar rotinas." );
									continue;
								}
								
								if( linguagem == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "linguagem" );
									continue;
								}
								
								if( rotina == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "rotina" );
									continue;
								}
								
								ScriptEngine processador = getProcessador( linguagem );
								
								if( processador == null ){
									enviar( Erro.LINGUAGEM_DESCONHECIDA, CopaibaException.class, "Linguagem desconhecida." );
									continue;
								}
								
								if( transformador != null ){
									rotina = transformador.transformar( usuario, linguagem, rotina );
								}
								
								if( auditor != null && ! auditor.aprovar( usuario, linguagem, rotina ) ){
									enviar( Erro.ROTINA_AUDITORIA, CopaibaException.class, "Rotina desaprovada." );
									continue;
								}
								
								try{

									Object resultado = null;
									
									try{
										publicarObjetos();
										tmp.reset();
										contexto.setWriter( tmpWriter );
										contexto.setErrorWriter( tmpWriter );
										resultado = processador.eval( rotina, contexto );
									}finally{
										tmpWriter.flush();
										contexto.setWriter( null );
										contexto.setErrorWriter( null );
										despublicarObjetos();
									}
									
									if( json ){
										if( conversor == null ) conversor = JSONUtil.novoConversor();
										resultado = conversor.writeValueAsString( resultado );
									}
									
									if( resultado != null && ! ( resultado instanceof Serializable ) ){
										enviar( Erro.ROTINA_EXECUCAO, NotSerializableException.class, resultado.getClass().getName() );
										continue;
									}
									
									String impressao = imprimir ? new String( tmp.getBuffer(), 0, tmp.size(), "UTF-8" ) : null;
									tmp.reset();
									
									tmpSaida.comando( Comando.SUCESSO );
									tmpSaida.objeto( (Serializable) resultado );
									tmpSaida.texto( impressao );
									
								}catch( Throwable e ){
									enviar( Erro.ROTINA_EXECUCAO, e );
									continue;
								}
								
								tmp.writeTo( canalS );
								tmp.reset();
								
							}else if( comando == Comando.MENSAGEM ){
								
								String         msg_objeto = entrada.texto();
								String         msg_metodo = entrada.texto();
								boolean        json       = entrada.inteiro8() == 1;
								Serializable[] msg_params = (Serializable[]) entrada.objeto();
								
								if( ! permitirMensagem ){
									enviar( Erro.PERMISSAO, SecurityException.class, "Sem permissão para enviar mensagens para objetos." );
									continue;
								}

								if( msg_objeto == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "objeto" );
									continue;
								}
								
								if( msg_metodo == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "metodo" );
									continue;
								}
								
								Object objeto    = null;
								Object resultado = null;

								try{
									
									try{
										publicarObjetos();
										if( ! escopo.containsKey( msg_objeto ) ){
											enviar( Erro.MENSAGEM_EXECUCAO, CopaibaException.class, "Objeto desconhecido: " + msg_objeto );
											continue;
										}
										objeto = escopo.get( msg_objeto );
									}finally{
										despublicarObjetos();
									}
									
									Class<?>[] params = null;
									if( msg_params != null && msg_params.length > 0 ){
										params = new Class<?>[ msg_params.length ];
										for( int i = 0; i < params.length; i++ ){
											params[i] = msg_params[i].getClass();
										}
									}
									
									Method mensagem = null;
									
									try{
										mensagem = objeto.getClass().getMethod( msg_metodo, params );
									}catch( Exception e ){
										for( Method m : objeto.getClass().getMethods() ){
											if( m.getName().equals( msg_metodo ) &&
													m.getParameterCount() == params.length ){
												mensagem = m;
												break;
											}
										}
									}
									
									if( mensagem == null ){
										enviar( Erro.MENSAGEM_EXECUCAO, CopaibaException.class, "Método desconhecido: " + msg_metodo );
										continue;
									}
									
									resultado = mensagem.invoke( objeto, (Object[]) msg_params );
									
								}catch( Throwable e ){
									enviar( Erro.MENSAGEM_EXECUCAO, e );
									continue;
								}
								
								if( json ){
									if( conversor == null ) conversor = JSONUtil.novoConversor();
									resultado = conversor.writeValueAsString( resultado );
								}
								
								if( resultado != null && ! ( resultado instanceof Serializable ) ){
									enviar( Erro.MENSAGEM_EXECUCAO, NotSerializableException.class, resultado.getClass().getName() );
									continue;
								}
								
								saida.comando( Comando.SUCESSO );
								saida.objeto( (Serializable) resultado );
								
							}else if( comando == Comando.VARIAVEL_LEITURA ){
								
								String  variavel = entrada.texto();
								boolean json     = entrada.inteiro8() == 1;
								Object  valor    = null;
								
								if( ! permitirLeitura ){
									enviar( Erro.PERMISSAO, SecurityException.class, "Sem permissão para acessar variáveis." );
									continue;
								}
								
								if( variavel == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "variavel" );
									continue;
								}

								try{
									
									try{
										publicarObjetos();
										if( ! escopo.containsKey( variavel ) ){
											enviar( Erro.VARIAVEL_LEITURA, CopaibaException.class, "Objeto desconhecido: " + variavel );
											continue;
										}
										valor = escopo.get( variavel );
									}finally{
										despublicarObjetos();
									}
									
								}catch( Throwable e ){
									enviar( Erro.VARIAVEL_LEITURA, e );
									continue;
								}
								
								if( json ){
									if( conversor == null ) conversor = JSONUtil.novoConversor();
									valor = conversor.writeValueAsString( valor );
								}
								
								if( valor != null && ! ( valor instanceof Serializable ) ){
									enviar( Erro.VARIAVEL_LEITURA, NotSerializableException.class, valor.getClass().getName() );
									continue;
								}
								
								saida.comando( Comando.SUCESSO );
								saida.objeto( (Serializable) valor );
								
							}else if( comando == Comando.VARIAVEL_ESCRITA ){
								
								String variavel = entrada.texto();
								byte   tipo     = entrada.inteiro8();
								
								Object objeto = null;
								String classe = null;
								String json   = null;
								
								if( tipo == 0 ){
									objeto = entrada.objeto();
								}else{
									classe = entrada.texto();
									json   = entrada.texto();
								}
								
								if( ! permitirAtribuicao ){
									enviar( Erro.PERMISSAO, SecurityException.class, "Sem permissão para executar atribuições." );
									continue;
								}
								
								if( variavel == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "variavel" );
									continue;
								}
								
								if( tipo == 1 ){
									
									if( classe == null ){
										enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "classe" );
										continue;
									}
									
									if( conversor == null ) conversor = JSONUtil.novoConversor();
									objeto = json != null ? conversor.readValue( json, Class.forName( classe ) ) : null;
									
								}
								
								escopo.put( variavel, objeto );
								saida.comando( Comando.SUCESSO );
								
							}else if( comando == Comando.VARIAVEL_REMOCAO ){

								String variavel = entrada.texto();
								
								if( ! permitirRemocao ){
									enviar( Erro.PERMISSAO, SecurityException.class, "Sem permissão para executar remoções." );
									continue;
								}
								
								if( variavel == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "variavel" );
									continue;
								}
								
								escopo.remove( variavel );
								saida.comando( Comando.SUCESSO );
								
							}else if( comando == Comando.SOLICITACAO ){
								
								String sol_classe = entrada.texto();
								String sol_estado = entrada.texto();
								String sol_metodo = entrada.texto();
								
								if( ! permitirSolicitacao ){
									enviar( Erro.PERMISSAO, SecurityException.class, "Sem permissão para executar solicitações." );
									continue;
								}
								
								if( sol_classe == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "classe" );
									continue;
								}
								
								if( sol_estado == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "estado" );
									continue;
								}
								
								if( sol_metodo == null ){
									enviar( Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, "metodo" );
									continue;
								}
								
								if( auditor != null && ! auditor.aprovar( usuario, sol_classe ) ){
									enviar( Erro.SOLICITACAO_AUDITORIA, SecurityException.class, sol_classe );
									continue;
								}
								
								if( conversor == null ) conversor = JSONUtil.novoConversor();
								
								Class<?> classe = Class.forName( sol_classe );
								Object   objeto = conversor.readValue( sol_estado, classe );
								Method   metodo = classe.getMethod( sol_metodo );
								
								try{
									Method setCopaibaEstado = classe.getMethod( "set$CopaibaEstado", String.class );
									setCopaibaEstado.invoke( objeto, sol_estado );
								}catch( Exception e ){
								}

								Object resultadoObj = metodo.invoke( objeto );
								String resultadoStr = null;

								if( resultadoObj instanceof String ){
									resultadoStr = (String) resultadoObj;
									if( resultadoStr.startsWith( "[JSON]~" ) ){
										resultadoStr = resultadoStr.substring( 7 );
									}else{
										resultadoStr = null;
									}
								}

								if( resultadoStr == null ){
									resultadoStr = conversor.writeValueAsString( resultadoObj );
								}
								
								saida.comando( Comando.SUCESSO );
								saida.texto( resultadoStr );
							
							}else if( comando == Comando.VERIFICACAO ){
								saida.comando( Comando.SUCESSO );
								
							}else if( comando == Comando.FIM ){
								saida.comando( Comando.SUCESSO );
								
							}else{
								enviar( Erro.COMANDO_DESCONHECIDO, CopaibaException.class, "Comando desconhecido." );
								return;
							}
							
						}
					
					}catch( Throwable erro ){
						
						try{
							enviar( Erro.DESCONHECIDO, erro );
						}catch( Exception e ){
						}
						
					}finally{
						
						try{
							canal.fechar();
						}catch( Exception e ){
						}
						
						try{
							sessoes.remove( this );
						}catch( Exception e ){
						}
						
						canal     = null;
						canalE    = null;
						canalS    = null;
						entrada   = null;
						saida     = null;
						tmp       = null;
						tmpSaida  = null;
						tmpWriter = null;
						usuario   = null;
						contexto  = null;
						escopo    = null;
						fornecido = null;
						conversor = null;
						
					}
					
				}
				
			}.start();
			
		}
		
		private void publicarObjetos() throws CopaibaException {
			
			if( fornecido.size() > 0 ) despublicarObjetos();
			
			String[] copaiba_nomes;
			
			if( fornecedor != null ){
				
				Map<String,Object> objetos = new HashMap<String,Object>();
				fornecedor.fornecer( usuario, objetos );
				
				for( String nome : objetos.keySet() ){
					escopo.put( nome, objetos.get( nome ) );
					fornecido.add( nome );
				}
				
				copaiba_nomes = fornecido.toArray( new String[ fornecido.size() ] );
				
			}else{
				
				copaiba_nomes = new String[0];
				
			}

			escopo.put( "copaiba_versao"    , VERSAO );
			escopo.put( "copaiba_usuario"   , usuario.clonar() );
			escopo.put( "copaiba_nomes"     , copaiba_nomes );
			escopo.put( "copaiba_informacao", new InformacaoImpl() );
			escopo.put( "copaiba_assistente", assistente != null ? new ProtegidoAssistente( assistente ) : null );
			escopo.put( "copaiba_servico"   , new ServicoImpl() );
			
		}
		
		private void despublicarObjetos() throws CopaibaException {
			
			escopo.remove( "copaiba_versao"     );
			escopo.remove( "copaiba_usuario"    );
			escopo.remove( "copaiba_nomes"      );
			escopo.remove( "copaiba_informacao" );
			escopo.remove( "copaiba_assistente" );
			escopo.remove( "copaiba_servico"    );
			
			for( String nome : fornecido ) escopo.remove( nome );
			fornecido.clear();
			
		}
		
		private void enviar( Erro erro, Throwable e ) throws IOException {
			while( e.getCause() != null ) e = e.getCause();
			if( e instanceof CopaibaException ) erro = ((CopaibaException)e).getErro();
			enviar( erro, e.getClass(), e.getMessage() );
		}
		
		private void enviar( Erro erro, Class<? extends Throwable> classe, String mensagem ) throws IOException {
			saida.comando( Comando.ERRO );
			saida.inteiro32( erro.getCodigo() );
			saida.texto( classe.getName() );
			saida.texto( mensagem );
			entrada.comando();
		}
		
		private void processarInformacao() throws Exception {
			
			entrada  = new JSONEntrada( canalE );
			saida    = new JSONSaida( canalS );
			tmpSaida = new JSONSaida( tmp );
			
			byte[] cert_bytes = null;
			if( publicarCertificados ){
				for( Certificate cert : certificados ){
					tmpWriter.write( "-----BEGIN CERTIFICATE-----\n" );
					tmpWriter.write( Base64.getEncoder().encodeToString( cert.getEncoded() ) );
					tmpWriter.write( "\n-----END CERTIFICATE-----\n" );
				}
			}
			tmpWriter.flush();
			cert_bytes = tmp.toByteArray();
			
			tmp.reset();
			tmpSaida.comando( Comando.SUCESSO );
			tmpSaida.texto( "Copaíba" );
			tmpSaida.real32( Copaiba.VERSAO );
			
			tmpSaida.inteiro32( cert_bytes.length );
			if( cert_bytes.length > 0 ){
				tmpSaida.bytes( cert_bytes, 0, cert_bytes.length );
			}
			
			tmpSaida.inteiro32( linguagens.size() );
			for( String linguagem : linguagens ){
				tmpSaida.texto( linguagem );
			}
			
			tmp.writeTo( canalS );
			entrada.comando();
			
		}
		
		private void processarTransferencia( boolean leitura ) {
			
			TransferenciaInputStream  tis = new TransferenciaInputStream( canalE );
			TransferenciaOutputStream tos = new TransferenciaOutputStream( canalS );
			
			Transferencia    transf = null;
			FileInputStream  fis    = null;
			FileOutputStream fos    = null;
			
			boolean   positivo = false;
			Exception negativo = null;
			
			try{
				
				UUID id = UUID.fromString( tis.texto() );
				transf = transferencia.get( id );
				if( transf == null ) return;
				
				if( leitura ){
					
					long tamanho = transf.arquivo.length();
					tos.inteiro64( tamanho );
					
					fis = new FileInputStream( transf.arquivo );
					for( int i = 0; i < tamanho; i++ ){
						tos.write( fis.read() );
					}
					
					tis.logico();
					
				}else{
					
					long tamanho = tis.inteiro64();
					
					fos = new FileOutputStream( transf.arquivo );
					for( int i = 0; i < tamanho; i++ ){
						fos.write( tis.read() );
					}
					
					tos.logico( true );
					
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
				
				if( transf != null ){
					if( positivo && transf.exito != null ){
						try{ transf.exito.notificar( transf.arquivo, null ); }catch( Exception e ){}
					}
					if( negativo != null && transf.erro != null ){
						try{ transf.erro.notificar( transf.arquivo, negativo ); }catch( Exception e ){}
					}
					transferencia.remove( transf.id );
					transf = null;
				}
				
			}
			
		}
		
	}
	
	private class InformacaoImpl implements Informacao {
		
		@Override
		public float getVersao() {
			return VERSAO;
		}
		
		@Override
		@SuppressWarnings( "unchecked" )
		public Collection<String> getLinguagens() {
			return (Collection<String>) linguagens.clone();
		}
		
		@Override
		@SuppressWarnings( "unchecked" )
		public Collection<Certificate> getCertificados() {
			if( publicarCertificados ){
				return (Collection<Certificate>) certificados.clone();				
			}else{
				return Collections.<Certificate>emptySet();
			}
		}
		
	}
	
	private class Transferencia {
		
		private UUID id;
		
		private File arquivo;
		
		private Notificacao<File,?> exito;
		
		private Notificacao<File,Throwable> erro;
		
		private long criacao;
		
	}
	
	private class ServicoImpl implements Servico {
		
		@Override
		public UUID registrarTransferencia( File arquivo, Notificacao<File,?> exito, Notificacao<File,Throwable> erro ) throws CopaibaException {
			if( ! permitirTransferencia ) throw new SecurityException( "Sem permissão para executar transferências." );
			if( arquivo == null ) throw new IllegalArgumentException();
			if( arquivo.length() > 0 ){
				usuarioAutorizado( "Copaiba.transferencia.leitura" );				
			}else{
				usuarioAutorizado( "Copaiba.transferencia.escrita" );	
			}
			Transferencia t = new Transferencia();
			t.id = UUID.randomUUID();
			t.arquivo = arquivo;
			t.exito = exito;
			t.erro = erro;
			t.criacao = System.currentTimeMillis();
			transferencia.put( t.id, t );
			return t.id;
		}
		
	}

	/**
	 * Processador de requisições expressas.
	 */
	private class SessaoExpressa extends ChannelInboundHandlerAdapter {

		private static final long TEMPO_MAXIMO = 20000; // 20 segundos
		private static final int  CARGA_MAXIMA = 2 * 1024 * 1024; // 2 megabytes
		
		private ByteBuf entrada, saida;
		private Charset codificacao;

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
			entrada     = null;
			saida       = null;
			codificacao = null;
		}

		@Override
		public void userEventTriggered( ChannelHandlerContext ctx, Object evt ) throws Exception {
			if( evt instanceof IdleStateEvent ){
				enviarErro( ctx, Erro.TEMPO_EXCEDIDO, null, null );
			}
		}

		@Override
		public void channelRead( ChannelHandlerContext ctx, Object msg ) {

			ByteBuf bb    = (ByteBuf) msg;
			int     bytes = entrada.readableBytes() + bb.readableBytes();

			try{
				if( bytes > CARGA_MAXIMA ){
					enviarErro( ctx, Erro.TAMANHO_EXCEDIDO, null, null );
					return;
				}
				entrada.writeBytes(bb);
				if( bytes < 4 ) return;
			}finally{
				bb.release();
			}

			int ultimo = entrada.readerIndex() + bytes - 1;

			if( entrada.getByte(ultimo  ) != MARCACAO_FIM[3] ) return;
			if( entrada.getByte(ultimo-1) != MARCACAO_FIM[2] ) return;
			if( entrada.getByte(ultimo-2) != MARCACAO_FIM[1] ) return;
			if( entrada.getByte(ultimo-3) != MARCACAO_FIM[0] ) return;

			entrada.writerIndex( entrada.writerIndex() - 4 );

			String comando = obterLinha();
			comando = comando != null && comando.length() <= 10 ? comando.toUpperCase() : "";

			if( comando.equals( "SOLICITAR" ) ){
				comandoSolicitar( ctx );
			}else{
				enviarErro( ctx, Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, comando );
			}

		}

		@Override
		public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) {
			enviarErro( ctx, Erro.DESCONHECIDO, cause.getClass(), cause.getMessage() );
		}

		private String obterLinha() {
			int bytes = entrada.readableBytes();
			if( bytes == 0 ) return null;
			int inicio = entrada.readerIndex();
			int fim    = inicio + bytes;
			int n = entrada.indexOf( inicio, fim, (byte) '\n' );
			if( n == -1 ) return null;
			if( n == inicio ) return "";
			int total = n - inicio;
			boolean retc = entrada.getByte( n - 1 ) == (byte) '\r';
			if( retc ){
				total--;
				if( total == 0 ) return "";
			}
			String str = entrada.toString( inicio, total, codificacao );
			entrada.skipBytes( total + ( retc ? 2 : 1 ) );
			return str;
		}

		private void enviarResposta( ChannelHandlerContext ctx, String resposta ) {
			saida.writeCharSequence( resposta, codificacao );
			ctx.writeAndFlush( saida ).addListener( ChannelFutureListener.CLOSE );
		}

		private void enviarErro( ChannelHandlerContext ctx, Erro erro, Class<?> classe, String mensagem ) {
			
			if( erro == null ) erro = Erro.DESCONHECIDO;

			JSON json = new JSON()
				.put( "exito", false )
				.put( "codigo", erro.getCodigo() )
				.put( "_copaiba_erro_codigo", erro.getCodigo() )
				.put( "_copaiba_erro_nome", erro.toString() )
				.put( "_copaiba_erro_classe", classe != null ? classe.getName() : null )
				.put( "_copaiba_erro_mensagem", mensagem != null ? mensagem : null );
			
			enviarResposta( ctx, json.toString() );
			
		}

		private void comandoSolicitar( ChannelHandlerContext ctx ) {

			if( ! permitirSolicitacao ){
				enviarErro( ctx, Erro.PERMISSAO, SecurityException.class, "Sem permissão para executar solicitações." );
				return;
			}

			String sol_classe = obterLinha();
			String sol_metodo = obterLinha();
			String sol_estado = entrada.toString(codificacao);

			if( sol_classe == null ){
				enviarErro( ctx, Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, sol_classe );
				return;
			}

			if( sol_metodo == null ){
				enviarErro( ctx, Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, sol_metodo );
				return;
			}

			if( sol_estado == null ){
				enviarErro( ctx, Erro.ARGUMENTO_INVALIDO, IllegalArgumentException.class, sol_estado );
				return;
			}

			try{
				if( auditor != null && ! auditor.aprovar( null, sol_classe ) ){
					enviarErro( ctx, Erro.SOLICITACAO_AUDITORIA, SecurityException.class, sol_classe );
					return;
				}
			}catch( Exception e ){
				enviarErro( ctx, Erro.SOLICITACAO_AUDITORIA, e.getClass(), e.getMessage() );
				return;
			}

			try{

				ObjectMapper conversor = JSONUtil.novoConversor();
				
				Class<?> classe = Class.forName( sol_classe );
				Object   objeto = conversor.readValue( sol_estado, classe );
				Method   metodo = classe.getMethod( sol_metodo );
				
				try{
					Method setCopaibaEstado = classe.getMethod( "set$CopaibaEstado", String.class );
					setCopaibaEstado.invoke( objeto, sol_estado );
				}catch( Exception e ){
				}
	
				Object resultadoObj = metodo.invoke( objeto );
				String resultadoStr = null;
	
				if( resultadoObj instanceof String ){
					resultadoStr = (String) resultadoObj;
					if( resultadoStr.startsWith( "[JSON]~" ) ){
						resultadoStr = resultadoStr.substring( 7 );
					}else{
						resultadoStr = null;
					}
				}
	
				if( resultadoStr == null ){
					resultadoStr = conversor.writeValueAsString( resultadoObj );
				}
				
				enviarResposta( ctx, resultadoStr );

			}catch( Exception e ){
				enviarErro( ctx, Erro.DESCONHECIDO, e.getClass(), e.getMessage() );
			}

		}

	}
	
}

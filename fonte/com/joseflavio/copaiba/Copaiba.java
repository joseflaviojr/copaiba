
/*
 *  Copyright (C) 2016-2018 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2018 José Flávio de Souza Dias Júnior
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseflavio.copaiba.util.GroovyApenasAuditor;
import com.joseflavio.copaiba.util.SimplesAutenticador;
import com.joseflavio.copaiba.util.SimplesFornecedor;
import com.joseflavio.copaiba.util.TempoLimiteTransformador;
import com.joseflavio.urucum.comunicacao.Consumidor;
import com.joseflavio.urucum.comunicacao.Notificacao;
import com.joseflavio.urucum.comunicacao.Servidor;
import com.joseflavio.urucum.comunicacao.SocketServidor;
import com.joseflavio.urucum.json.JSONUtil;

import javax.script.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
	
	private static final Map<Thread,Sessao> sessoes;
	
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
	
	static {
		
		sessoes = Collections.synchronizedMap( new HashMap<Thread,Sessao>( 1000 ) );
		
	}
	
	/**
	 * {@link Copaiba} completo.
	 * @param autenticador {@link Autenticador}, opcional. Exemplo: {@link SimplesAutenticador}.
	 * @param transformador {@link Transformador}, opcional. Exemplo: {@link TempoLimiteTransformador}.
	 * @param auditor {@link Auditor}, opcional. Exemplo: {@link GroovyApenasAuditor}.
	 * @param fornecedor {@link Fornecedor}, opcional. Exemplo: {@link SimplesFornecedor}.
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.criar"</code>
	 * @see #abrir(Servidor)
	 */
	public Copaiba( Autenticador autenticador, Transformador transformador, Auditor auditor, Fornecedor fornecedor ) {
		
		seguranca( "Copaiba.criar" );
		
		this.gerenciador   = new ScriptEngineManager();
		this.linguagens    = new HashSet<String>();
		this.processadores = new HashMap<String,ScriptEngine>();
		this.certificados  = new HashSet<Certificate>();
		this.autenticador  = autenticador;
		this.transformador = transformador;
		this.auditor       = auditor;
		this.fornecedor    = fornecedor;
		this.transferencia = Collections.synchronizedMap( new HashMap<UUID,Transferencia>( 128 ) );
		
		for( ScriptEngineFactory sef : this.gerenciador.getEngineFactories() ){
			this.linguagens.add( sef.getLanguageName() );
		}
		
		try{
			String jks = System.getProperty( "javax.net.ssl.keyStore" );
			File jksArquivo = jks != null ? new File( jks ) : null;
			if( jksArquivo != null && jksArquivo.exists() ){
				String jksSenha = System.getProperty( "javax.net.ssl.keyStorePassword" );
				KeyStore ks = KeyStore.getInstance( KeyStore.getDefaultType() );
				FileInputStream fis = new FileInputStream( jksArquivo );
				try{ ks.load( fis, jksSenha.toCharArray() ); }finally{ fis.close(); }
				Enumeration<String> nomes = ks.aliases();
				while( nomes.hasMoreElements() ){
					this.certificados.add( ks.getCertificate( nomes.nextElement() ) );
				}
			}
		}catch( Exception e ){
		}
		
	}
	
	/**
	 * {@link Copaiba} inicialmente sem {@link Autenticador}, {@link Transformador} e {@link Auditor}.
	 * @see SimplesFornecedor
	 * @see #abrir(Servidor)
	 * @see #Copaiba(Autenticador, Transformador, Auditor, Fornecedor)
	 */
	public Copaiba( Fornecedor fornecedor ) {
		this( null, null, null, fornecedor );
	}
	
	/**
	 * {@link Copaiba} inicialmente sem {@link Autenticador}, {@link Transformador}, {@link Auditor} e {@link Fornecedor}.
	 * @see #abrir(Servidor)
	 * @see #Copaiba(Autenticador, Transformador, Auditor, Fornecedor)
	 */
	public Copaiba() {
		this( null, null, null, null );
	}
	
	/**
	 * Ativa a recepção de {@link CopaibaConexao conexões}.<br>
	 * Aconselha-se, antes de chamar este método, proteger as classes e objetos públicos,
	 * através de {@link SecurityManager}. Para mais detalhes, veja {@link Fornecedor}.
	 * @param servidor Meio de comunicação para receber as {@link CopaibaConexao conexões}.
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.abrir"</code>
	 * @see Autenticador
	 * @see Auditor
	 * @see Fornecedor
	 * @see #isAberta()
	 * @see #fechar()
	 */
	public final void abrir( Servidor servidor ) throws CopaibaException {
		
		seguranca( "Copaiba.abrir" );
		
		if( servidor == null || ! servidor.isAberto() ) throw new IllegalArgumentException( "O servidor deve estar aberto." );
		
		this.servidor = servidor;
		
		if( copaibaRecepcao != null ){
			new Thread(){
				@Override
				public void run() {
					try{
						copaibaRecepcao.pronta( Copaiba.this );
					}catch( Exception e ){
					}					
				}
			}.start();
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
		
		while( true ){
			try{
				Consumidor canal = servidor.aceitar();
				Sessao sessao = new Sessao( canal );
				sessao.iniciar();
			}catch( Exception e ){
			}
		}
		
	}
	
	/**
	 * {@link Copaiba} baseada em {@link SocketServidor}.
	 * @see #abrir(Servidor)
	 */
	public final void abrir( int porta, boolean segura ) throws CopaibaException {
		try{
			abrir( new SocketServidor( porta, segura ) );
		}catch( IOException e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
	}
	
	/**
	 * {@link Copaiba} baseada em {@link SocketServidor}, porta 8884 e segurança TLS/SSL desativada.
	 * @see #abrir(Servidor)
	 */
	public final void abrir() throws CopaibaException {
		try{
			abrir( new SocketServidor( 8884, false ) );
		}catch( IOException e ){
			throw new CopaibaException( Erro.DESCONHECIDO, e );
		}
	}
	
	/**
	 * A {@link Copaiba} está {@link #abrir(Servidor) aberta}?
	 * @see #abrir(Servidor)
	 * @see Servidor#isAberto()
	 * @see #fechar()
	 */
	public final boolean isAberta() {
		return servidor != null && servidor.isAberto();
	}
	
	/**
	 * {@link Servidor#fechar() Fecha} o {@link Servidor} e encerra todas as atividades internas, inclusive
	 * as {@link CopaibaConexao conexões} ativas.<br>
	 * Contudo, poderá {@link #abrir(Servidor)} novamente.
	 * @throws SecurityException {@link RuntimePermission} <code>"Copaiba.fechar"</code>
	 * @see #abrir(Servidor)
	 * @see #isAberta()
	 */
	public final void fechar() {

		seguranca( "Copaiba.fechar" );
		
		if( servidor == null ) return;
		
		if( servidor.isAberto() ){
			try{
				servidor.fechar();
			}catch( Exception e ){
			}
		}
		
		if( transferenciaManutencao != null && ! transferenciaManutencao.isDone() ){
			try{
				transferenciaManutencao.cancel( true );
			}catch( Exception e ){
			}
		}
		
		servidor = null;
		processadores.clear();
		transferencia.clear();
		
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
		
		private Consumidor canal;
		private InputStream canalE;
		private OutputStream canalS;
		
		private Entrada entrada;
		private Saida saida;
		
		private TemporariaOutputStream tmp;
		private Saida tmpSaida;
		private Writer tmpWriter;
		
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
									enviar( Erro.SOLICITACAO_AUDITORIA, CopaibaException.class, "Classe desaprovada." );
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
								
								String resultado = conversor.writeValueAsString( metodo.invoke( objeto ) );
								
								saida.comando( Comando.SUCESSO );
								saida.texto( resultado );
							
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
					tmpWriter.write( DatatypeConverter.printBase64Binary( cert.getEncoded() ) );
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
	
}

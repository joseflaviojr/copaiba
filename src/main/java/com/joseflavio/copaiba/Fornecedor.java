
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

import java.security.Permission;
import java.security.Policy;
import java.util.Map;

import com.joseflavio.copaiba.util.SimplesFornecedor;
import com.joseflavio.urucum.seguranca.SimplesPolicy;

/**
 * Fornecedor de {@link Object objetos} para serem acessados através da {@link Copaiba}.<br>
 * É importante que sejam adotadas {@link Policy#getPolicy() políticas} de
 * {@link System#getSecurityManager() segurança}
 * em todos os métodos de objetos críticos, como, por exemplo,
 * invocar {@link Copaiba#usuarioAutorizado(String)} ou
 * {@link Copaiba#usuarioPertence(String)}.<br>
 * O {@link SecurityManager} pode ser ativado através do parâmetro <code>-Djava.security.manager</code>
 * da JVM, ou em tempo de execução através de {@link System#setSecurityManager(SecurityManager)}.<br>
 * As {@link Permission permissões} de acesso, como a {@link CopaibaPermission},
 * são agregadas numa {@link Policy}, a qual pode ser especificada através de
 * {@link Policy#setPolicy(Policy)} (utilizando, por exemplo, uma {@link SimplesPolicy}) ou
 * através de arquivo externo (parâmetro <code>-Djava.security.policy</code> da JVM).
 * @author José Flávio de Souza Dias Júnior
 * @see SecurityManager
 * @see Policy
 * @see CopaibaPermission
 * @see SimplesFornecedor
 * @see SimplesPolicy
 */
public interface Fornecedor {
	
	/**
	 * {@link Object Objetos} que um determinado {@link Usuario} pode acessar nesse instante.<br>
	 * Esses objetos serão fornecidos para a
	 * {@link CopaibaConexao#executar(String, String, java.io.Writer) execução}
	 * corrente do {@link Usuario}.<br>
	 * Utilizadores deste fornecimento:<br>
	 * <ul>
	 * <li>{@link CopaibaConexao#executar(String, String, java.io.Writer)}</li>
	 * <li>{@link CopaibaConexao#obter(String, String, boolean, java.io.Serializable...)}</li>
	 * <li>{@link CopaibaConexao#obter(String)}</li>
	 * </ul>
	 * @param usuario {@link Usuario} que receberá os objetos.
	 * @param objetos Objetos a serem fornecidos. {@link Map}: nome da variável = objeto.
	 */
	void fornecer( Usuario usuario, Map<String,Object> objetos ) throws CopaibaException;
	
}

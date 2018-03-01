
/*
 *  Copyright (C) 2016-2018 Jos� Fl�vio de Souza Dias J�nior
 *  
 *  This file is part of Copa�ba - <http://joseflavio.com/copaiba/>.
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
 *  Este arquivo � parte de Copa�ba - <http://joseflavio.com/copaiba/>.
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

package com.joseflavio.copaiba;

import java.security.Permission;
import java.security.Policy;
import java.util.Map;

import com.joseflavio.copaiba.util.SimplesFornecedor;
import com.joseflavio.urucum.seguranca.SimplesPolicy;

/**
 * Fornecedor de {@link Object objetos} para serem acessados atrav�s da {@link Copaiba}.<br>
 * � importante que sejam adotadas {@link Policy#getPolicy() pol�ticas} de
 * {@link System#getSecurityManager() seguran�a}
 * em todos os m�todos de objetos cr�ticos, como, por exemplo,
 * invocar {@link Copaiba#usuarioAutorizado(String)} ou
 * {@link Copaiba#usuarioPertence(String)}.<br>
 * O {@link SecurityManager} pode ser ativado atrav�s do par�metro <code>-Djava.security.manager</code>
 * da JVM, ou em tempo de execu��o atrav�s de {@link System#setSecurityManager(SecurityManager)}.<br>
 * As {@link Permission permiss�es} de acesso, como a {@link CopaibaPermission},
 * s�o agregadas numa {@link Policy}, a qual pode ser especificada atrav�s de
 * {@link Policy#setPolicy(Policy)} (utilizando, por exemplo, uma {@link SimplesPolicy}) ou
 * atrav�s de arquivo externo (par�metro <code>-Djava.security.policy</code> da JVM).
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @see SecurityManager
 * @see Policy
 * @see CopaibaPermission
 * @see SimplesFornecedor
 * @see SimplesPolicy
 */
public interface Fornecedor {
	
	/**
	 * {@link Object Objetos} que um determinado {@link Usuario} pode acessar nesse instante.<br>
	 * Esses objetos ser�o fornecidos para a
	 * {@link CopaibaConexao#executar(String, String, java.io.Writer) execu��o}
	 * corrente do {@link Usuario}.<br>
	 * Utilizadores deste fornecimento:<br>
	 * <ul>
	 * <li>{@link CopaibaConexao#executar(String, String, java.io.Writer)}</li>
	 * <li>{@link CopaibaConexao#obter(String, String, boolean, java.io.Serializable...)}</li>
	 * <li>{@link CopaibaConexao#obter(String)}</li>
	 * </ul>
	 * @param usuario {@link Usuario} que receber� os objetos.
	 * @param objetos Objetos a serem fornecidos. {@link Map}: nome da vari�vel = objeto.
	 */
	void fornecer( Usuario usuario, Map<String,Object> objetos ) throws CopaibaException;
	
}

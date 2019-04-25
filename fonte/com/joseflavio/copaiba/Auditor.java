
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

/**
 * Auditor de rotinas de {@link CopaibaConexao#executar(String, String, java.io.Writer) execução}.
 * @author José Flávio de Souza Dias Júnior
 * @see Autenticador
 */
public interface Auditor {

	/**
	 * Valida e verifica uma rotina de {@link CopaibaConexao#executar(String, String, java.io.Writer) execução} de um {@link Usuario}.
	 * @param usuario {@link Usuario} em atuação. null == anônimo.
	 * @return <code>true</code>, se rotina devidamente aprovada.
	 * @see CopaibaConexao#executar(String, String, java.io.Writer)
	 */
	boolean aprovar( Usuario usuario, String linguagem, String rotina ) throws CopaibaException;
	
	/**
	 * Aprova ou não a utilização de uma {@link Class classe} pelo {@link Usuario}.<br>
	 * Recursos sensíveis a esta auditoria:<br>
	 * {@link CopaibaConexao#solicitar(String, String, String)}
	 * @param usuario {@link Usuario} em atuação. null == anônimo.
	 * @param classe {@link Class#getName()} desejada.
	 */
	boolean aprovar( Usuario usuario, String classe ) throws CopaibaException;
	
}


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

/**
 * Auditor de rotinas de {@link CopaibaConexao#executar(String, String, java.io.Writer) execu��o}.
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @see Autenticador
 */
public interface Auditor {

	/**
	 * Valida e verifica uma rotina de {@link CopaibaConexao#executar(String, String, java.io.Writer) execu��o} de um {@link Usuario}.
	 * @param usuario {@link Usuario} em atua��o. null == an�nimo.
	 * @return <code>true</code>, se rotina devidamente aprovada.
	 * @see CopaibaConexao#executar(String, String, java.io.Writer)
	 */
	boolean aprovar( Usuario usuario, String linguagem, String rotina ) throws CopaibaException;
	
	/**
	 * Aprova ou n�o a utiliza��o de uma {@link Class classe} pelo {@link Usuario}.<br>
	 * Recursos sens�veis a esta auditoria:<br>
	 * {@link CopaibaConexao#solicitar(String, String, String)}
	 * @param usuario {@link Usuario} em atua��o. null == an�nimo.
	 * @param classe {@link Class#getName()} desejada.
	 */
	boolean aprovar( Usuario usuario, String classe ) throws CopaibaException;
	
}

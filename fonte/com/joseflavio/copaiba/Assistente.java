
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

import java.net.URL;

/**
 * Assistente de programa��o remota.
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
public interface Assistente {
	
	/**
	 * Nome completo de classes acess�veis por {@link CopaibaConexao},
	 * n�o se restringindo apenas a {@link Class}'s.<br>
	 * Exemplos:<br>
	 * <pre>
	 * java.lang.String
	 * java.util.Arrays
	 * </pre>
	 */
	String[] getClasses();
	
	/**
	 * Verifica se a classe indicada est� entre as {@link #getClasses()}.
	 */
	boolean contemClasse( String classe );
	
	/**
	 * Membros de um classe, incluindo heran�a: atributos, m�todos, classes internas, etc.<br>
	 * Exemplos da classe {@link java.lang.String}:<br>
	 * <pre>
	 * java.lang.String&#64;CASE_INSENSITIVE_ORDER
	 * java.lang.String&#35;String(char[],int,int)
	 * java.lang.String&#35;contains(java.lang.CharSequence)
	 * java.lang.String&#35;format(java.util.Locale,java.lang.String,java.lang.Object...)
	 * java.lang.Object&#35;notify()
	 * </pre><br>
	 * Divisores poss�veis:<br>
	 * <pre>
	 * &#64; = atributo
	 * &#35; = m�todo
	 * &#38; = classe interna
	 * </pre> 
	 * @param classe Nome completo da classe, conforme padr�o {@link Class#getName()}.
	 * @return <code>null</code>, caso a classe n�o seja encontrada.
	 * @see #getClasses()
	 */
	String[] getMembros( String classe );

	/**
	 * {@link URL} da documenta��o de um componente do sistema.
	 * @param componente Identifica��o do componente. Formatos poss�veis: {@link #getClasses()} e {@link #getMembros(String)}.
	 * @return <code>null</code>, caso inexista a documenta��o ou o componente.
	 */
	URL getDocumentacao( String componente );
	
}

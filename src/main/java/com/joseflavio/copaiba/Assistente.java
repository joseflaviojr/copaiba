
/*
 *  Copyright (C) 2016-2020 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2020 José Flávio de Souza Dias Júnior
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

import java.net.URL;

/**
 * Assistente de programação remota.
 * @author José Flávio de Souza Dias Júnior
 */
public interface Assistente {
	
	/**
	 * Nome completo de classes acessíveis por {@link CopaibaConexao},
	 * não se restringindo apenas a {@link Class}'s.<br>
	 * Exemplos:<br>
	 * <pre>
	 * java.lang.String
	 * java.util.Arrays
	 * </pre>
	 */
	String[] getClasses();
	
	/**
	 * Verifica se a classe indicada está entre as {@link #getClasses()}.
	 */
	boolean contemClasse( String classe );
	
	/**
	 * Membros de um classe, incluindo herança: atributos, métodos, classes internas, etc.<br>
	 * Exemplos da classe {@link java.lang.String}:<br>
	 * <pre>
	 * java.lang.String&#64;CASE_INSENSITIVE_ORDER
	 * java.lang.String&#35;String(char[],int,int)
	 * java.lang.String&#35;contains(java.lang.CharSequence)
	 * java.lang.String&#35;format(java.util.Locale,java.lang.String,java.lang.Object...)
	 * java.lang.Object&#35;notify()
	 * </pre><br>
	 * Divisores possíveis:<br>
	 * <pre>
	 * &#64; = atributo
	 * &#35; = método
	 * &#38; = classe interna
	 * </pre> 
	 * @param classe Nome completo da classe, conforme padrão {@link Class#getName()}.
	 * @return <code>null</code>, caso a classe não seja encontrada.
	 * @see #getClasses()
	 */
	String[] getMembros( String classe );

	/**
	 * {@link URL} da documentação de um componente do sistema.
	 * @param componente Identificação do componente. Formatos possíveis: {@link #getClasses()} e {@link #getMembros(String)}.
	 * @return <code>null</code>, caso inexista a documentação ou o componente.
	 */
	URL getDocumentacao( String componente );
	
}

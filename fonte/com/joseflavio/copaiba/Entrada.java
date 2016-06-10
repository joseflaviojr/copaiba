
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

import java.io.IOException;
import java.io.Serializable;

/**
 * Interface para recepção de dados pela {@link CopaibaConexao}.
 * @author José Flávio de Souza Dias Júnior
 */
interface Entrada {
	
	Serializable objeto() throws IOException;
	
	String texto() throws IOException;
	
	byte inteiro8() throws IOException;
	
	short inteiro16() throws IOException;
	
	int inteiro32() throws IOException;
	
	long inteiro64() throws IOException;
	
	float real32() throws IOException;
	
	double real64() throws IOException;
	
	char caractere() throws IOException;
	
	boolean logico() throws IOException;
	
	void bytes( byte[] o, int inicio, int total ) throws IOException;
	
	Comando comando() throws IOException;
	
	/**
	 * Desconsidera o subsequente conteúdo escrito com {@link Saida#objeto(Serializable)}.
	 */
	void saltarObjeto() throws IOException;
	
	/**
	 * Desconsidera o subsequente conteúdo escrito com {@link Saida#texto(String)}.
	 */
	void saltarTexto() throws IOException;
	
}

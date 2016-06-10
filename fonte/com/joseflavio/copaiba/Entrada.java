
/*
 *  Copyright (C) 2016 Jos� Fl�vio de Souza Dias J�nior
 *  
 *  This file is part of Copa�ba - <http://www.joseflavio.com/copaiba/>.
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
 *  Direitos Autorais Reservados (C) 2016 Jos� Fl�vio de Souza Dias J�nior
 * 
 *  Este arquivo � parte de Copa�ba - <http://www.joseflavio.com/copaiba/>.
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

import java.io.IOException;
import java.io.Serializable;

/**
 * Interface para recep��o de dados pela {@link CopaibaConexao}.
 * @author Jos� Fl�vio de Souza Dias J�nior
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
	 * Desconsidera o subsequente conte�do escrito com {@link Saida#objeto(Serializable)}.
	 */
	void saltarObjeto() throws IOException;
	
	/**
	 * Desconsidera o subsequente conte�do escrito com {@link Saida#texto(String)}.
	 */
	void saltarTexto() throws IOException;
	
}

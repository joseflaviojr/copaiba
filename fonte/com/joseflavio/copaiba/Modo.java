
/*
 *  Copyright (C) 2016-2018 Jos� Fl�vio de Souza Dias J�nior
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
 *  Direitos Autorais Reservados (C) 2016-2018 Jos� Fl�vio de Souza Dias J�nior
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

import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Modos de transfer�ncia de dados do protocolo de comunica��o da {@link Copaiba}.
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
public enum Modo {
	
	/**
	 * Transfer�ncia de dados conforme o padr�o de {@link Serializable serializa��o} da Java.
	 * @see ObjectOutputStream
	 */
	JAVA,
	
	/**
	 * Transfer�ncia de dados no formato JSON (JavaScript Object Notation).
	 * @see ObjectMapper
	 */
	JSON;
	
}

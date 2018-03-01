
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

package com.joseflavio.copaiba.util;

import java.util.Map;

import com.joseflavio.copaiba.CopaibaException;
import com.joseflavio.copaiba.Fornecedor;
import com.joseflavio.copaiba.Usuario;

/**
 * {@link Fornecedor} simples, que {@link Fornecedor#fornecer(Usuario, Map) fornece} um �nico objeto a todos {@link Usuario}s.
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
public class SimplesFornecedor implements Fornecedor {
	
	private String nome;
	
	private Object objeto;
	
	/**
	 * @param nome Nome que identifica o objeto.
	 * @param objeto Objeto a ser fornecido.
	 */
	public SimplesFornecedor( String nome, Object objeto ) {
		if( nome == null || objeto == null ) throw new IllegalArgumentException();
		this.nome = nome;
		this.objeto = objeto;
	}

	@Override
	public void fornecer( Usuario usuario, Map<String,Object> objetos ) throws CopaibaException {
		objetos.put( nome, objeto );
	}
	
}

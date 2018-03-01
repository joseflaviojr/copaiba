
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

import java.util.Date;

/**
 * Usu�rio de {@link Copaiba}.
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @see Autenticador
 */
public final class Usuario {
	
	Autenticador autenticador;
	
	private String nome;
	
	private float versao;
	
	private Date inicio;
	
	Usuario( Autenticador autenticador, String nome, float versao, Date inicio ) {
		this.autenticador = autenticador;
		this.nome         = nome;
		this.versao       = versao;
		this.inicio       = inicio;
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null || ! ( obj instanceof Usuario ) ) return false;
		Usuario o = (Usuario) obj;
		if( ! nome.equals(   o.nome ) )   return false;
		if(   versao !=      o.versao )   return false;
		if( ! inicio.equals( o.inicio ) ) return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return (int)( nome.hashCode() + versao + inicio.hashCode() );
	}
	
	@Override
	public String toString() {
		return nome;
	}
	
	Usuario clonar() {
		return new Usuario( autenticador, nome, versao, inicio );
	}
	
	/**
	 * Nome do usu�rio.
	 */
	public String getNome() {
		return nome;
	}
	
	/**
	 * Vers�o da {@link Copaiba} que o usu�rio est� usando.
	 */
	public float getVersao() {
		return versao;
	}
	
	/**
	 * {@link Date Data/hora} em que iniciou a sess�o do usu�rio.
	 */
	public Date getInicio() {
		return inicio;
	}

}


/*
 *  Copyright (C) 2016-2021 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2021 José Flávio de Souza Dias Júnior
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

import java.util.Date;

/**
 * Usuário de {@link Copaiba}.
 * @author José Flávio de Souza Dias Júnior
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
	 * Nome do usuário.
	 */
	public String getNome() {
		return nome;
	}
	
	/**
	 * Versão da {@link Copaiba} que o usuário está usando.
	 */
	public float getVersao() {
		return versao;
	}
	
	/**
	 * {@link Date Data/hora} em que iniciou a sessão do usuário.
	 */
	public Date getInicio() {
		return inicio;
	}

}

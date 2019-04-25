
/*
 *  Copyright (C) 2016-2019 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2019 José Flávio de Souza Dias Júnior
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

package com.joseflavio.copaiba.teste;

import java.util.Calendar;

import com.joseflavio.copaiba.Copaiba;

/**
 * @author José Flávio de Souza Dias Júnior
 */
public class TesteCompartilhado {

	private long valor;

	private long contador = 1;

	public TesteCompartilhado() {
	}

	public TesteCompartilhado( long valor ) {
		super();
		this.valor = valor;
	}

	public synchronized long setValor( long valor ) {
		this.valor = valor;
		return valor;
	}

	public long getValor() {
		return valor;
	}

	public synchronized void incrementarContador() {
		this.contador++;
	}

	public synchronized void setContador( long contador ) {
		this.contador = contador;
	}

	public long getContador() {
		return contador;
	}

	public double somar( double x, double y ) {
		return x + y;
	}

	public int somarIdades( TestePessoa p1, TestePessoa p2 ) {
		Calendar cal = Calendar.getInstance();
		int anoAtual = cal.get( Calendar.YEAR );
		cal.setTime( p1.getNascimento() );
		int ano1 = cal.get( Calendar.YEAR );
		cal.setTime( p2.getNascimento() );
		int ano2 = cal.get( Calendar.YEAR );
		return ( anoAtual - ano1 ) + ( anoAtual - ano2 );
	}

	public void restricaoRecurso1() {
		Copaiba.usuarioAutorizado( "recurso.1" );
	}

	public void restricaoRecurso2() {
		Copaiba.usuarioAutorizado( "recurso.2" );
	}

	public void restricaoRecurso3() {
		Copaiba.usuarioAutorizado( "recurso.3" );
	}

	public void restricaoAdmin1() {
		Copaiba.usuarioAutorizado( "admin.1" );
	}

	public void restricaoAdmin2() {
		Copaiba.usuarioAutorizado( "admin.2" );
	}

	public void restricaoGrupo1() {
		Copaiba.usuarioPertence( "grupo.1" );
	}

	public void restricaoGrupo2() {
		Copaiba.usuarioPertence( "grupo.2" );
	}

}

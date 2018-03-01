
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

package com.joseflavio.copaiba.teste;

import java.util.Calendar;

import com.joseflavio.copaiba.Copaiba;

/**
 * @author Jos� Fl�vio de Souza Dias J�nior
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


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

package com.joseflavio.copaiba.teste;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
public class TestePessoa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String nome;
	
	private Date nascimento;
	
	private TestePessoa mae;
	
	public TestePessoa() {
	}
	
	public TestePessoa( String nome, Date nascimento, TestePessoa mae ) {
		this.nome = nome;
		this.nascimento = nascimento;
		this.mae = mae;
	}
	
	private boolean igual( Object a, Object b ) {
		if( a == b ) return true;
		if( a == null || b == null ) return false;
		return a.equals( b );
	}
	
	@Override
	public boolean equals( Object obj ) {
		if( ! ( obj instanceof TestePessoa ) ) return false;
		TestePessoa o = (TestePessoa) obj;
		return 	igual( nome, o.nome ) &&
				igual( nascimento, o.nascimento ) &&
				igual( mae, o.mae );
	}

	public String getNome() {
		return nome;
	}
	
	public void setNome( String nome ) {
		this.nome = nome;
	}

	public Date getNascimento() {
		return nascimento;
	}
	
	public void setNascimento( Date nascimento ) {
		this.nascimento = nascimento;
	}

	public TestePessoa getMae() {
		return mae;
	}
	
	public void setMae( TestePessoa mae ) {
		this.mae = mae;
	}
	
}

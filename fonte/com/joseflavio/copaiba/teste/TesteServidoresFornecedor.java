
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

import java.util.Date;
import java.util.Map;

import com.joseflavio.copaiba.Copaiba;
import com.joseflavio.copaiba.CopaibaException;
import com.joseflavio.copaiba.Fornecedor;
import com.joseflavio.copaiba.Usuario;

/**
 * {@link Fornecedor} para {@link CopaibaTestes}.
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
class TesteServidoresFornecedor implements Fornecedor {
	
	public static final TesteCompartilhado compartilhado = new TesteCompartilhado(1);
	
	private Copaiba copaiba;
	
	TesteServidoresFornecedor( Copaiba copaiba ) {
		this.copaiba = copaiba;
	}

	@Override
	public void fornecer( Usuario usuario, Map<String,Object> objetos ) throws CopaibaException {
		
		if( usuario.getNome().equals( "jose" ) ){
			
			TestePessoa p = new TestePessoa();
			p.setNome( "Jos� Teste" );
			p.setNascimento( new Date() );
			p.setMae( new TestePessoa( "M�e do Jos� Teste", new Date(), null ) );
			objetos.put( "pessoa", p );
			
		}else if( usuario.getNome().equals( "maria" ) ){
			
			TestePessoa p = new TestePessoa();
			p.setNome( "Maria Teste" );
			p.setNascimento( new Date() );
			objetos.put( "pessoa", p );
			
		}
		
		objetos.put( "compartilhado", compartilhado );
		objetos.put( "gerencia", new Gerencia() );
		
	}
	
	public class Gerencia {
		
		public void setPermitirRotina( boolean permitirRotina ) {
			copaiba.setPermitirRotina( permitirRotina );
		}
		
	}
	
}
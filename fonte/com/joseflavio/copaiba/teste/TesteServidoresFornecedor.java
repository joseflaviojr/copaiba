
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

package com.joseflavio.copaiba.teste;

import java.util.Date;
import java.util.Map;

import com.joseflavio.copaiba.Copaiba;
import com.joseflavio.copaiba.CopaibaException;
import com.joseflavio.copaiba.Fornecedor;
import com.joseflavio.copaiba.Usuario;

/**
 * {@link Fornecedor} para {@link CopaibaTestes}.
 * @author José Flávio de Souza Dias Júnior
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
			p.setNome( "José Teste" );
			p.setNascimento( new Date() );
			p.setMae( new TestePessoa( "Mãe do José Teste", new Date(), null ) );
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
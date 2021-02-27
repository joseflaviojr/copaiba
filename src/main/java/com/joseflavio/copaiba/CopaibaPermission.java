
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

import java.security.BasicPermission;
import java.security.Permission;

/**
 * {@link Permission} específica para a arquitetura {@link Copaiba}.<br>
 * Formato do {@link Permission#getName() nome}: <code>[[$]acessante]@recurso</code><br>
 * Composições possíveis para o {@link Permission#getName() nome}:<br>
 * <ul>
 * <li>Nome de {@link Usuario} + "@" + Nome de recurso</li>
 * <li>"$" + Nome de grupo de {@link Usuario}s + "@" + Nome de recurso</li>
 * <li>"@" + Nome de recurso <strong>(qualquer usuário)</strong></li>
 * </ul>
 * Exemplos de {@link Permission#getName() nomes}:<br>
 * <ul>
 * <li>maria@abc.def.Exemplo</li>
 * <li>joao@abc.def.*</li>
 * <li>$gerentes@abc.def.Exemplo</li>
 * <li>@abc.def.Exemplo</li>
 * </ul>
 * Em <code>sun.security.provider.PolicyFile</code>, segue-se os formatos:<br>
 * <ul>
 * <li>permission com.joseflavio.copaiba.CopaibaPermission "usuario@recurso";</li>
 * <li>permission com.joseflavio.copaiba.CopaibaPermission "$grupo@recurso";</li>
 * <li>permission com.joseflavio.copaiba.CopaibaPermission "@recurso";</li>
 * </ul>
 * @author José Flávio de Souza Dias Júnior
 */
public final class CopaibaPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;
	
	private String acessante;
	
	private String recurso;
	
	private boolean grupo;
	
	/**
	 * @see CopaibaPermission
	 */
	public CopaibaPermission( String nome ) {
		
		super( nome );
		
		String[] partes = nome.split( "@" );
		if( partes.length != 2 ) throw new IllegalArgumentException();
		
		acessante = partes[0];
		recurso = partes[1];
		
		if( recurso.isEmpty() ) throw new IllegalArgumentException();
		
		if( acessante.startsWith( "$" ) ){
			if( acessante.length() == 1 ) throw new IllegalArgumentException();
			acessante = acessante.substring( 1 );
			grupo = true;
		}else{
			if( acessante.isEmpty() ) acessante = null;
			grupo = false;
		}
		
	}
	
	/**
	 * <code>acessante@recurso</code>
	 */
	public CopaibaPermission( String acessante, String recurso ) {
		this( ( acessante != null ? acessante : "" ) + "@" + ( recurso != null ? recurso : "" ) );
	}
	
	@Override
	public boolean implies( Permission p ) {
		
		if( ! ( p instanceof CopaibaPermission ) ) return false;
		
		CopaibaPermission cp = (CopaibaPermission) p;
		
		if( this.acessante == null && cp.acessante != null ){
			return new CopaibaPermission( cp.acessante, this.recurso ).implies( cp );
		}else{
			return super.implies( cp );
		}
		
	}
	
	public String getAcessante() {
		return acessante;
	}
	
	public String getRecurso() {
		return recurso;
	}
	
	public boolean isGrupo() {
		return grupo;
	}
	
}

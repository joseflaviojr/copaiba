
/*
 *  Copyright (C) 2016-2018 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2018 José Flávio de Souza Dias Júnior
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

package com.joseflavio.copaiba.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.joseflavio.copaiba.Autenticador;
import com.joseflavio.copaiba.CopaibaException;
import com.joseflavio.copaiba.Usuario;

/**
 * {@link Autenticador} simples, que gerencia senhas em memória.
 * @author José Flávio de Souza Dias Júnior
 */
public class SimplesAutenticador implements Autenticador {
	
	private Map<String,Especificacao> usuarios = Collections.synchronizedMap( new HashMap<String,Especificacao>() );

	@Override
	public boolean autenticar( String usuario, String senha ) throws CopaibaException {
		if( usuario == null || senha == null ) throw new IllegalArgumentException();
		Especificacao espec = usuarios.get( usuario );
		return espec != null && espec.senha.equals( senha );
	}
	
	@Override
	public boolean pertence( Usuario usuario, String grupo ) throws CopaibaException {
		if( usuario == null || grupo == null ) throw new IllegalArgumentException();
		Especificacao espec = usuarios.get( usuario.getNome() );
		if( espec != null ){
			for( String g : espec.grupos ){
				if( g.equals( grupo ) ) return true;
			}
		}
		return false;
	}
	
	@Override
	public String[] getGrupos( Usuario usuario ) throws CopaibaException {
		if( usuario == null ) throw new IllegalArgumentException();
		Especificacao espec = usuarios.get( usuario.getNome() );
		return espec != null ? espec.grupos.clone() : new String[0];
	}
	
	/**
	 * Adiciona ou atualiza informações sobre um usuário.
	 * @param usuario Veja {@link Usuario#getNome()}.
	 * @param grupos Veja {@link Autenticador#pertence(Usuario, String)}.
	 */
	public void definirUsuario( String usuario, String senha, String... grupos ) {
		if( usuario == null || senha == null || grupos == null ) throw new IllegalArgumentException();
		usuarios.put( usuario, new Especificacao( senha, grupos ) );
	}
	
	/**
	 * Remove um usuário anteriormente {@link #definirUsuario(String, String, String[]) definido}.
	 */
	public void removerUsuario( String usuario ) {
		if( usuario == null ) throw new IllegalArgumentException();
		usuarios.remove( usuario );
	}
	
	private class Especificacao {

		private String senha;
		private String[] grupos;
		
		private Especificacao( String senha, String[] grupos ) {
			this.senha = senha;
			this.grupos = grupos;
		}
		
	}
	
}


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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.joseflavio.copaiba.Autenticador;
import com.joseflavio.copaiba.CopaibaException;
import com.joseflavio.copaiba.Usuario;

/**
 * {@link Autenticador} simples, que gerencia senhas em mem�ria.
 * @author Jos� Fl�vio de Souza Dias J�nior
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
	 * Adiciona ou atualiza informa��es sobre um usu�rio.
	 * @param usuario Veja {@link Usuario#getNome()}.
	 * @param grupos Veja {@link Autenticador#pertence(Usuario, String)}.
	 */
	public void definirUsuario( String usuario, String senha, String... grupos ) {
		if( usuario == null || senha == null || grupos == null ) throw new IllegalArgumentException();
		usuarios.put( usuario, new Especificacao( senha, grupos ) );
	}
	
	/**
	 * Remove um usu�rio anteriormente {@link #definirUsuario(String, String, String[]) definido}.
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

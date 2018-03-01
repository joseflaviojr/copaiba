
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

package com.joseflavio.copaiba.teste;

import java.io.File;
import java.security.Policy;

import com.joseflavio.copaiba.CopaibaPermission;
import com.joseflavio.urucum.seguranca.SimplesPolicy;

/**
 * {@link SecurityManager} para {@link CopaibaTestes}.
 * @author José Flávio de Souza Dias Júnior
 */
final class TesteSeguranca {
	
	private SimplesPolicy politica;

	public TesteSeguranca() {
		
		politica = new SimplesPolicy();
		
		politica.adicionar( new CopaibaPermission( "jose@admin.*" ) );
		politica.adicionar( new CopaibaPermission( "jose@recurso.1" ) );
		politica.adicionar( new CopaibaPermission( "jose@Copaiba.transferencia.*" ) );
		politica.adicionar( new CopaibaPermission( "$grupo.2@recurso.2" ) );
		politica.adicionar( new CopaibaPermission( "@recurso.3" ) );
		
		politica.adicionar( new groovy.security.GroovyCodeSourcePermission( "/groovy/script" ) );
		politica.adicionar( new java.io.FilePermission( "<<ALL FILES>>", "read" ) );
		politica.adicionar( new java.io.FilePermission( System.getProperty( "user.home" ) + File.separator + "-", "read, write" ) );
		politica.adicionar( new java.lang.RuntimePermission( "accessClassInPackage.sun.reflect" ) );
		politica.adicionar( new java.lang.RuntimePermission( "accessClassInPackage.sun.security.*" ) );
		politica.adicionar( new java.lang.RuntimePermission( "accessDeclaredMembers" ) );
		politica.adicionar( new java.lang.RuntimePermission( "Copaiba.*" ) );
		politica.adicionar( new java.lang.RuntimePermission( "createClassLoader" ) );
		politica.adicionar( new java.lang.RuntimePermission( "getProtectionDomain" ) );
		politica.adicionar( new java.lang.RuntimePermission( "loadLibrary.sunec" ) );
		politica.adicionar( new java.lang.reflect.ReflectPermission( "suppressAccessChecks" ) );
		politica.adicionar( new java.net.SocketPermission( "", "listen,accept,connect,resolve" ) );
		politica.adicionar( new java.security.SecurityPermission( "putProviderProperty.SunJCE" ) );
		politica.adicionar( new java.util.PropertyPermission( "ANTLR_DO_NOT_EXIT", "read" ) );
		politica.adicionar( new java.util.PropertyPermission( "ANTLR_USE_DIRECT_CLASS_LOADING", "read" ) );
		politica.adicionar( new java.util.PropertyPermission( "antlr.ast", "read" ) );
		politica.adicionar( new java.util.PropertyPermission( "groovy.*", "read" ) );
		politica.adicionar( new java.util.PropertyPermission( "java.vm.name", "read" ) );
		politica.adicionar( new java.util.PropertyPermission( "java.vm.vendor", "read" ) );
		politica.adicionar( new java.util.PropertyPermission( "javax.net.ssl.*", "read" ) );
		politica.adicionar( new java.util.PropertyPermission( "os.name", "read" ) );
		politica.adicionar( new java.util.PropertyPermission( "user.home", "read" ) );
		politica.adicionar( new java.util.logging.LoggingPermission( "control", null ) );
		
	}
	
	public final void ativar() {
		
		Policy.setPolicy( politica );
		
		System.setSecurityManager( new SecurityManager(){
			@Override
			public void checkExit( int status ) {
				throw new SecurityException();
			}
		});
		
	}
	
}


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

package com.joseflavio.copaiba.util;

import java.lang.reflect.ReflectPermission;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseflavio.copaiba.Copaiba;

/**
 * Utilitários relacionados à {@link Copaiba}.
 * @author José Flávio de Souza Dias Júnior
 */
public class CopaibaUtil {
	
	public static ObjectMapper novoConversorJSON() {
		
		ObjectMapper conversor = new ObjectMapper();
		
		conversor.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
		conversor.configure( DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true );
		conversor.configure( DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true );
		
		SecurityManager sm = System.getSecurityManager();
		if( sm != null ){
			try{
				sm.checkPermission( new ReflectPermission( "suppressAccessChecks" ) );
			}catch( Exception e ){
				conversor.disable( MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS );
			}
		}
		
		return conversor;
		
	}
	
}

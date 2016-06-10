
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

package com.joseflavio.copaiba.util;

import java.lang.reflect.ReflectPermission;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseflavio.copaiba.Copaiba;

/**
 * Utilit�rios relacionados � {@link Copaiba}.
 * @author Jos� Fl�vio de Souza Dias J�nior
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

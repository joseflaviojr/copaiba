
/*
 *  Copyright (C) 2016-2019 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2019 José Flávio de Souza Dias Júnior
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

import java.io.IOException;
import java.io.Serializable;

/**
 * Interface para envio de dados pela {@link CopaibaConexao}.
 * @author José Flávio de Souza Dias Júnior
 */
interface Saida {
	
	void objeto( Serializable o ) throws IOException;
	
	void texto( String o ) throws IOException;
	
	void inteiro8( byte o ) throws IOException;
	
	void inteiro16( short o ) throws IOException;
	
	void inteiro32( int o ) throws IOException;
	
	void inteiro64( long o ) throws IOException;
	
	void real32( float o ) throws IOException;
	
	void real64( double o ) throws IOException;
	
	void caractere( char o ) throws IOException;
	
	void logico( boolean o ) throws IOException;
	
	void bytes( byte[] o, int inicio, int total ) throws IOException;
	
	void comando( Comando o ) throws IOException;
	
}

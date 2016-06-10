
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

package com.joseflavio.copaiba;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author José Flávio de Souza Dias Júnior
 */
class TransferenciaOutputStream extends OutputStream {
	
	private OutputStream os;
	
	public TransferenciaOutputStream( OutputStream os ) {
		this.os = os;
	}
	
	@Override
	public void write( int b ) throws IOException {
		os.write( b );
	}
	
	@Override
	public void flush() throws IOException {
		os.flush();
	}
	
	@Override
	public void close() throws IOException {
		os = null;
	}

    public void inteiro32( int o ) throws IOException {
    		os.write( ( o >>> 24 ) & 0xff );
		os.write( ( o >>> 16 ) & 0xff );
		os.write( ( o >>>  8 ) & 0xff );
		os.write( ( o >>>  0 ) & 0xff );
    }
	
    public void inteiro64( long o ) throws IOException {
		os.write( (int) ( ( o >>> 56 ) & 0xff ) );
		os.write( (int) ( ( o >>> 48 ) & 0xff ) );
		os.write( (int) ( ( o >>> 40 ) & 0xff ) );
		os.write( (int) ( ( o >>> 32 ) & 0xff ) );
		os.write( (int) ( ( o >>> 24 ) & 0xff ) );
		os.write( (int) ( ( o >>> 16 ) & 0xff ) );
		os.write( (int) ( ( o >>>  8 ) & 0xff ) );
		os.write( (int) ( ( o >>>  0 ) & 0xff ) );
    }
	
	public void caractere( char o ) throws IOException {
		os.write( ( o >>> 8 ) & 0xff );
		os.write( ( o >>> 0 ) & 0xff );
	}
	
	public void texto( String o ) throws IOException {
		inteiro32( o.length() );
		for( char c : o.toCharArray() ){
			caractere( c );
		}
	}
	
	public void logico( boolean o ) throws IOException {
		os.write( o ? 1 : 0 );
	}
	
}


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

package com.joseflavio.copaiba;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
class TransferenciaInputStream extends InputStream {
	
	private InputStream is;
	
	public TransferenciaInputStream( InputStream is ) {
		this.is = is;
	}
	
	@Override
	public int available() throws IOException {
		return is.available();
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}
	
	@Override
	public long skip( long n ) throws IOException {
		return is.skip( n );
	}
	
	@Override
	public void close() throws IOException {
		is = null;
	}
	
    public int inteiro32() throws IOException {
		return  ( ( is.read() & 0xff ) << 24 ) |
				( ( is.read() & 0xff ) << 16 ) |
				( ( is.read() & 0xff ) <<  8 ) |
				( ( is.read() & 0xff ) <<  0 );
    }
	
    public long inteiro64() throws IOException {
		return  ( ( (long) is.read() & 0xff ) << 56 ) |
				( ( (long) is.read() & 0xff ) << 48 ) |
				( ( (long) is.read() & 0xff ) << 40 ) |
				( ( (long) is.read() & 0xff ) << 32 ) |
				( ( (long) is.read() & 0xff ) << 24 ) |
				( ( (long) is.read() & 0xff ) << 16 ) |
				( ( (long) is.read() & 0xff ) <<  8 ) |
				( ( (long) is.read() & 0xff ) <<  0 );
    }
	
	public char caractere() throws IOException {
		return  (char)(
				( ( is.read() & 0xff ) << 8 ) |
				( ( is.read() & 0xff ) << 0 )
				);
	}
	
	public String texto() throws IOException {
		int total = inteiro32();
		char[] texto = new char[total];
		for( int i = 0; i < total; i++ ) texto[i] = caractere();
		return new String( texto );
	}
	
	public boolean logico() throws IOException {
		return is.read() == 1;
	}
	
}

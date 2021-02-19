
/*
 *  Copyright (C) 2016-2020 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2020 José Flávio de Souza Dias Júnior
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
import java.io.InputStream;

/**
 * @author José Flávio de Souza Dias Júnior
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

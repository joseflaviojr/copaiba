
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

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * {@link Entrada} baseada em {@link ObjectInputStream}.
 * @author José Flávio de Souza Dias Júnior
 */
class JavaEntrada implements Entrada {
	
	private DataInputStream dis;
	
	public JavaEntrada( InputStream is ) throws IOException {
		this.dis = new DataInputStream( is );
	}

	@Override
	public Serializable objeto() throws IOException {
		try{
			dis.skipBytes( 4 );
			return (Serializable) new ObjectInputStream( dis ).readObject();
		}catch( IOException e ){
			throw e;
		}catch( Exception e ){
			throw new IOException( e );
		}
	}

	@Override
	public String texto() throws IOException {
		int total = dis.readInt();
		if( total < 0 ) return null;
		if( total == 0 ) return "";
		byte[] bytes = new byte[total];
		bytes( bytes, 0, bytes.length );
		return new String( bytes, "UTF-8" );
	}

	@Override
	public byte inteiro8() throws IOException {
		return dis.readByte();
	}

	@Override
	public short inteiro16() throws IOException {
		return dis.readShort();
	}

	@Override
	public int inteiro32() throws IOException {
		return dis.readInt();
	}

	@Override
	public long inteiro64() throws IOException {
		return dis.readLong();
	}

	@Override
	public float real32() throws IOException {
		return dis.readFloat();
	}

	@Override
	public double real64() throws IOException {
		return dis.readDouble();
	}

	@Override
	public char caractere() throws IOException {
		return dis.readChar();
	}

	@Override
	public boolean logico() throws IOException {
		return dis.readBoolean();
	}

	@Override
	public void bytes( byte[] o, int inicio, int total ) throws IOException {
		int b;
		while( total-- > 0 ){
			b = dis.read();
			if( b == -1 ) throw new EOFException();
			o[inicio++] = (byte) b;
		}
	}
	
	@Override
	public Comando comando() throws IOException {
		return Comando.getComando( dis.readByte() );
	}
	
	@Override
	public void saltarObjeto() throws IOException {
		dis.skipBytes( dis.readInt() );
	}
	
	@Override
	public void saltarTexto() throws IOException {
		int total = dis.readInt();
		if( total > 0 ) dis.skipBytes( total );
	}

}

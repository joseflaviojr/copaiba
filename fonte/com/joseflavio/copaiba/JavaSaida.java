
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

package com.joseflavio.copaiba;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * {@link Saida} baseada em {@link ObjectOutputStream}.
 * @author José Flávio de Souza Dias Júnior
 */
class JavaSaida implements Saida {
	
	private DataOutputStream dos;
	
	private TemporariaOutputStream tmp;
	
	public JavaSaida( OutputStream os ) throws IOException {
		this.dos = new DataOutputStream( os );
		this.tmp = new TemporariaOutputStream( 1024 );
	}

	@Override
	public void objeto( Serializable o ) throws IOException {
		tmp.reset();
		new ObjectOutputStream( tmp ).writeObject( o );
		dos.writeInt( tmp.size() );
		tmp.writeTo( dos );
		dos.flush();
		tmp.reset();
	}

	@Override
	public void texto( String o ) throws IOException {
		if( o != null ){
			if( o.length() > 0 ){
				byte[] bytes = o.getBytes( "UTF-8" );
				dos.writeInt( bytes.length );
				bytes( bytes, 0, bytes.length );
			}else{
				dos.writeInt( 0 );	
			}
		}else{
			dos.writeInt( -1 );
		}
		dos.flush();
	}

	@Override
	public void inteiro8( byte o ) throws IOException {
		dos.writeByte( o );
		dos.flush();
	}

	@Override
	public void inteiro16( short o ) throws IOException {
		dos.writeShort( o );
		dos.flush();
	}

	@Override
	public void inteiro32( int o ) throws IOException {
		dos.writeInt( o );
		dos.flush();
	}

	@Override
	public void inteiro64( long o ) throws IOException {
		dos.writeLong( o );
		dos.flush();
	}

	@Override
	public void real32( float o ) throws IOException {
		dos.writeFloat( o );
		dos.flush();
	}

	@Override
	public void real64( double o ) throws IOException {
		dos.writeDouble( o );
		dos.flush();
	}

	@Override
	public void caractere( char o ) throws IOException {
		dos.writeChar( o );
		dos.flush();
	}

	@Override
	public void logico( boolean o ) throws IOException {
		dos.writeBoolean( o );
		dos.flush();
	}

	@Override
	public void bytes( byte[] o, int inicio, int total ) throws IOException {
		while( total-- > 0 ) dos.write( o[inicio++] );
		dos.flush();
	}
	
	@Override
	public void comando( Comando o ) throws IOException {
		dos.writeByte( o.getCodigo() );
		dos.flush();
	}

}

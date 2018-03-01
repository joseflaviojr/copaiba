
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

import java.io.File;
import java.util.UUID;

import com.joseflavio.urucum.comunicacao.Notificacao;

/**
 * Servi�os especiais da {@link Copaiba}.
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
public interface Servico {
	
	/**
	 * Registra um {@link File arquivo} para transfer�ncia atrav�s de exclusiva {@link CopaibaConexao}.
	 * @param arquivo {@link File Arquivo} de escrita (upload) ou de leitura (download, se {@link File#length() tamanho} > 0).
	 * @param exito {@link Notificacao} executada ap�s a conclus�o efetiva da transfer�ncia. Opcional.
	 * @param erro {@link Notificacao} executada ap�s algum impeditivo da transfer�ncia. Opcional.
	 * @return {@link UUID} da transfer�ncia a ser realizada.
	 * @throws SecurityException {@link Copaiba#usuarioAutorizado(String)} para <code>"Copaiba.transferencia.escrita"</code> ou <code>"Copaiba.transferencia.leitura"</code>
	 */
	UUID registrarTransferencia( File arquivo, Notificacao<File,?> exito, Notificacao<File,Throwable> erro ) throws CopaibaException;
	
}

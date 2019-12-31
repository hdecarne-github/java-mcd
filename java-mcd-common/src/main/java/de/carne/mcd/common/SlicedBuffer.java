/*
 * Copyright (c) 2019 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.mcd.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

class SlicedBuffer implements SeekableByteChannel {

	private final ByteBuffer buffer;

	SlicedBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void close() throws IOException {
		// nothing to do here
	}

	@Override
	public synchronized int read(@Nullable ByteBuffer dst) throws IOException {
		Objects.requireNonNull(dst);

		int dstRemaining = dst.remaining();
		int bufferRemaining = this.buffer.remaining();
		int read;

		if (bufferRemaining == 0) {
			read = -1;
		} else if (bufferRemaining <= dstRemaining) {
			dst.put(this.buffer);
			read = bufferRemaining;
		} else {
			ByteBuffer limitedBuffer = this.buffer.duplicate();

			limitedBuffer.limit(limitedBuffer.position() + dstRemaining);
			dst.put(limitedBuffer);
			read = dstRemaining;
			this.buffer.position(this.buffer.position() + read);
		}
		return read;
	}

	@Override
	public int write(@Nullable ByteBuffer src) throws IOException {
		throw new NonWritableChannelException();
	}

	@Override
	public long position() throws IOException {
		return this.buffer.position();
	}

	@Override
	public SeekableByteChannel position(long newPosition) throws IOException {
		int bufferLimit = this.buffer.limit();

		if (bufferLimit < newPosition) {
			throw new NonWritableChannelException();
		}
		this.buffer.position((int) Math.min(newPosition, bufferLimit));
		return this;
	}

	@Override
	public long size() throws IOException {
		return this.buffer.limit();
	}

	@Override
	public SeekableByteChannel truncate(long size) throws IOException {
		throw new NonWritableChannelException();
	}

}

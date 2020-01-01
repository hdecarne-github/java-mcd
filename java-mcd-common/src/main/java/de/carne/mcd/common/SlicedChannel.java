/*
 * Copyright (c) 2019-2020 Holger de Carne and contributors, All Rights Reserved.
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

import de.carne.boot.check.Check;

class SlicedChannel implements SeekableByteChannel {

	private final SeekableByteChannel channel;
	private final long start;
	private final long length;
	private long position;

	SlicedChannel(SeekableByteChannel channel, long start, long length) {
		this.channel = channel;
		this.start = start;
		this.length = length;
		this.position = 0;
	}

	@Override
	public boolean isOpen() {
		return this.channel.isOpen();
	}

	@Override
	public void close() throws IOException {
		// nothing to do here
	}

	@Override
	public int read(@Nullable ByteBuffer dst) throws IOException {
		Objects.requireNonNull(dst);

		int readLimit = (int) Math.min(this.length - this.position, dst.remaining());
		int read = -1;

		if (readLimit > 0) {
			ByteBuffer limitedDst = dst.duplicate();

			limitedDst.limit(limitedDst.position() + readLimit);
			this.channel.position(this.start + this.position);
			read = this.channel.read(limitedDst);
			if (read > 0) {
				dst.position(dst.position() + read);
				this.position += read;
			}
		}
		return read;
	}

	@Override
	public int write(@Nullable ByteBuffer src) throws IOException {
		throw new NonWritableChannelException();
	}

	@Override
	public long position() throws IOException {
		return this.position;
	}

	@Override
	public SeekableByteChannel position(long newPosition) throws IOException {
		Check.isTrue(newPosition >= 0);

		if (newPosition > this.length) {
			throw new NonWritableChannelException();
		}
		this.position = newPosition;
		return this;
	}

	@Override
	public long size() throws IOException {
		return this.length;
	}

	@Override
	public SeekableByteChannel truncate(long size) throws IOException {
		throw new NonWritableChannelException();
	}

}

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.airlift.slice;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.concurrent.TimeUnit;

import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.toByteArray;
import static io.airlift.slice.Slices.wrappedBuffer;

@SuppressWarnings("MethodMayBeStatic")
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(5)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class BenchmarkSliceIndexOf
{
    @Benchmark
    public Object indexOf(BenchmarkData data)
            throws Throwable
    {
        return data.data.indexOf(data.needle);
    }

    @State(Scope.Thread)
    public static class BenchmarkData
    {
        @Param({"10", "100", "200", "500", "1000", "1500", "10000"})
        private int length;

        @Param({"1", "5", "10", "20", "50", "100"})
        private int needleLength;

        @Param({"true", "false"})
        private boolean match = true;

        private Slice data;
        private Slice needle;

        @Setup(Level.Iteration)
        public void setup()
                throws Throwable
        {
            data = Slices.copyOf(wrappedBuffer(toByteArray(getResource("corpus.txt"))), 0, length);
            needle = Slices.allocate(needleLength);
            needle.fill((byte) 'A');
            if (match) {
                data.setBytes(data.length() - needle.length(), needle);
            }
        }
    }

    public static void main(String[] args)
            throws Throwable
    {
        Options options = new OptionsBuilder()
                .verbosity(VerboseMode.NORMAL)
                .include(".*" + BenchmarkSliceIndexOf.class.getSimpleName() + ".*")
                .build();
        new Runner(options).run();
    }
}

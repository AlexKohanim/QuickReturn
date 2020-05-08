package p006ti.modules.titanium.stream;

import java.io.IOException;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.p005io.TiStream;
import p006ti.modules.titanium.BufferProxy;

/* renamed from: ti.modules.titanium.stream.StreamModule */
public class StreamModule extends KrollModule {
    public static final int MODE_APPEND = 2;
    public static final int MODE_READ = 0;
    public static final int MODE_WRITE = 1;

    public Object createStream(KrollDict params) {
        Object source = params.get("source");
        Object rawMode = params.get("mode");
        if (!(rawMode instanceof Number)) {
            throw new IllegalArgumentException("Unable to create stream, invalid mode");
        }
        int mode = ((Number) rawMode).intValue();
        if (source instanceof TiBlob) {
            if (mode == 0) {
                return new BlobStreamProxy((TiBlob) source);
            }
            throw new IllegalArgumentException("Unable to create a blob stream in a mode other than read");
        } else if (source instanceof BufferProxy) {
            return new BufferStreamProxy((BufferProxy) source, mode);
        } else {
            throw new IllegalArgumentException("Unable to create a stream for the specified argument");
        }
    }

    public void read(Object[] args) {
        int offset = 0;
        KrollFunction resultsCallback = null;
        if (args.length != 3 && args.length != 5) {
            throw new IllegalArgumentException("Invalid number of arguments");
        } else if (args[0] instanceof TiStream) {
            TiStream sourceStream = args[0];
            if (args[1] instanceof BufferProxy) {
                BufferProxy buffer = args[1];
                int length = buffer.getLength();
                if (args.length == 3) {
                    if (args[2] instanceof KrollFunction) {
                        resultsCallback = args[2];
                    } else {
                        throw new IllegalArgumentException("Invalid callback argument");
                    }
                } else if (args.length == 5) {
                    if (args[2] instanceof Number) {
                        offset = args[2].intValue();
                        if (args[3] instanceof Number) {
                            length = args[3].intValue();
                            if (args[4] instanceof KrollFunction) {
                                resultsCallback = args[4];
                            } else {
                                throw new IllegalArgumentException("Invalid callback argument");
                            }
                        } else {
                            throw new IllegalArgumentException("Invalid length argument");
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid offset argument");
                    }
                }
                final TiStream fsourceStream = sourceStream;
                final BufferProxy fbuffer = buffer;
                final int foffset = offset;
                final int flength = length;
                final KrollFunction fResultsCallback = resultsCallback;
                new Thread(new Runnable() {
                    public void run() {
                        int bytesRead = -1;
                        int errorState = 0;
                        String errorDescription = "";
                        try {
                            bytesRead = fsourceStream.read(new Object[]{fbuffer, Integer.valueOf(foffset), Integer.valueOf(flength)});
                        } catch (IOException e) {
                            e.printStackTrace();
                            errorState = 1;
                            errorDescription = e.getMessage();
                        }
                        fResultsCallback.callAsync(StreamModule.this.getKrollObject(), (HashMap) StreamModule.this.buildRWCallbackArgs(fsourceStream, bytesRead, errorState, errorDescription));
                    }
                }).start();
                return;
            }
            throw new IllegalArgumentException("Invalid buffer argument");
        } else {
            throw new IllegalArgumentException("Invalid stream argument");
        }
    }

    public Object readAll(Object[] args) throws IOException {
        BufferProxy bufferArg = null;
        KrollFunction resultsCallback = null;
        if (args.length != 1 && args.length != 3) {
            throw new IllegalArgumentException("Invalid number of arguments");
        } else if (args[0] instanceof TiStream) {
            TiStream sourceStream = args[0];
            if (args.length == 3) {
                if (args[1] instanceof BufferProxy) {
                    bufferArg = args[1];
                    if (args[2] instanceof KrollFunction) {
                        resultsCallback = args[2];
                    } else {
                        throw new IllegalArgumentException("Invalid callback argument");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid buffer argument");
                }
            }
            if (args.length == 1) {
                BufferProxy buffer = new BufferProxy(1024);
                readAll(sourceStream, buffer, 0);
                return buffer;
            }
            final TiStream fsourceStream = sourceStream;
            final BufferProxy fbuffer = bufferArg;
            final KrollFunction fResultsCallback = resultsCallback;
            new Thread(new Runnable() {
                public void run() {
                    int errorState = 0;
                    String errorDescription = "";
                    if (fbuffer.getLength() < 1024) {
                        fbuffer.resize(1024);
                    }
                    try {
                        StreamModule.this.readAll(fsourceStream, fbuffer, 0);
                    } catch (IOException e) {
                        errorState = 1;
                        errorDescription = e.getMessage();
                    }
                    fResultsCallback.callAsync(StreamModule.this.getKrollObject(), (HashMap) StreamModule.this.buildRWCallbackArgs(fsourceStream, fbuffer.getLength(), errorState, errorDescription));
                }
            }).start();
            return null;
        } else {
            throw new IllegalArgumentException("Invalid stream argument");
        }
    }

    /* access modifiers changed from: private */
    public void readAll(TiStream sourceStream, BufferProxy buffer, int offset) throws IOException {
        int totalBytesRead = 0;
        while (true) {
            int bytesRead = sourceStream.read(new Object[]{buffer, Integer.valueOf(offset), Integer.valueOf(1024)});
            if (bytesRead == -1) {
                buffer.resize(totalBytesRead);
                return;
            }
            totalBytesRead += bytesRead;
            buffer.resize(totalBytesRead + 1024);
            offset += bytesRead;
        }
    }

    public void write(Object[] args) {
        int offset = 0;
        KrollFunction resultsCallback = null;
        if (args.length != 3 && args.length != 5) {
            throw new IllegalArgumentException("Invalid number of arguments");
        } else if (args[0] instanceof TiStream) {
            TiStream outputStream = args[0];
            if (args[1] instanceof BufferProxy) {
                BufferProxy buffer = args[1];
                int length = buffer.getLength();
                if (args.length == 3) {
                    if (args[2] instanceof KrollFunction) {
                        resultsCallback = args[2];
                    } else {
                        throw new IllegalArgumentException("Invalid callback argument");
                    }
                } else if (args.length == 5) {
                    if (args[2] instanceof Number) {
                        offset = args[2].intValue();
                        if (args[3] instanceof Number) {
                            length = args[3].intValue();
                            if (args[4] instanceof KrollFunction) {
                                resultsCallback = args[4];
                            } else {
                                throw new IllegalArgumentException("Invalid callback argument");
                            }
                        } else {
                            throw new IllegalArgumentException("Invalid length argument");
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid offset argument");
                    }
                }
                final TiStream foutputStream = outputStream;
                final BufferProxy fbuffer = buffer;
                final int foffset = offset;
                final int flength = length;
                final KrollFunction fResultsCallback = resultsCallback;
                new Thread(new Runnable() {
                    public void run() {
                        int bytesWritten = -1;
                        int errorState = 0;
                        String errorDescription = "";
                        try {
                            bytesWritten = foutputStream.write(new Object[]{fbuffer, Integer.valueOf(foffset), Integer.valueOf(flength)});
                        } catch (IOException e) {
                            e.printStackTrace();
                            errorState = 1;
                            errorDescription = e.getMessage();
                        }
                        fResultsCallback.callAsync(StreamModule.this.getKrollObject(), (HashMap) StreamModule.this.buildRWCallbackArgs(foutputStream, bytesWritten, errorState, errorDescription));
                    }
                }).start();
                return;
            }
            throw new IllegalArgumentException("Invalid buffer argument");
        } else {
            throw new IllegalArgumentException("Invalid stream argument");
        }
    }

    public int writeStream(Object[] args) throws IOException {
        KrollFunction resultsCallback = null;
        if (args.length != 3 && args.length != 4) {
            throw new IllegalArgumentException("Invalid number of arguments");
        } else if (args[0] instanceof TiStream) {
            TiStream inputStream = args[0];
            if (args[1] instanceof TiStream) {
                TiStream outputStream = args[1];
                if (args[2] instanceof Number) {
                    int maxChunkSize = args[2].intValue();
                    if (args.length == 4) {
                        if (args[3] instanceof KrollFunction) {
                            resultsCallback = args[3];
                        } else {
                            throw new IllegalArgumentException("Invalid callback argument");
                        }
                    }
                    if (args.length == 3) {
                        return writeStream(inputStream, outputStream, maxChunkSize);
                    }
                    final TiStream finputStream = inputStream;
                    final TiStream foutputStream = outputStream;
                    final int fmaxChunkSize = maxChunkSize;
                    final KrollFunction fResultsCallback = resultsCallback;
                    new Thread(new Runnable() {
                        public void run() {
                            int totalBytesWritten = 0;
                            int errorState = 0;
                            String errorDescription = "";
                            try {
                                totalBytesWritten = StreamModule.this.writeStream(finputStream, foutputStream, fmaxChunkSize);
                            } catch (IOException e) {
                                errorState = 1;
                                errorDescription = e.getMessage();
                            }
                            fResultsCallback.callAsync(StreamModule.this.getKrollObject(), (HashMap) StreamModule.this.buildWriteStreamCallbackArgs(finputStream, foutputStream, totalBytesWritten, errorState, errorDescription));
                        }
                    }).start();
                    return 0;
                }
                throw new IllegalArgumentException("Invalid max chunk size argument");
            }
            throw new IllegalArgumentException("Invalid output stream argument");
        } else {
            throw new IllegalArgumentException("Invalid input stream argument");
        }
    }

    /* access modifiers changed from: private */
    public int writeStream(TiStream inputStream, TiStream outputStream, int maxChunkSize) throws IOException {
        BufferProxy buffer = new BufferProxy(maxChunkSize);
        int totalBytesWritten = 0;
        while (true) {
            int bytesRead = inputStream.read(new Object[]{buffer, Integer.valueOf(0), Integer.valueOf(maxChunkSize)});
            if (bytesRead == -1) {
                return totalBytesWritten;
            }
            totalBytesWritten += outputStream.write(new Object[]{buffer, Integer.valueOf(0), Integer.valueOf(bytesRead)});
            buffer.clear();
        }
    }

    public void pump(Object[] args) {
        boolean isAsync = false;
        if (args.length != 3 && args.length != 4) {
            throw new IllegalArgumentException("Invalid number of arguments");
        } else if (args[0] instanceof TiStream) {
            TiStream inputStream = args[0];
            if (args[1] instanceof KrollFunction) {
                KrollFunction handler = args[1];
                if (args[2] instanceof Number) {
                    int maxChunkSize = args[2].intValue();
                    if (args.length == 4) {
                        if (args[3] instanceof Boolean) {
                            isAsync = args[3].booleanValue();
                        } else {
                            throw new IllegalArgumentException("Invalid async flag argument");
                        }
                    }
                    if (isAsync) {
                        final TiStream finputStream = inputStream;
                        final KrollFunction fHandler = handler;
                        final int fmaxChunkSize = maxChunkSize;
                        new Thread(new Runnable() {
                            public void run() {
                                StreamModule.this.pump(finputStream, fHandler, fmaxChunkSize);
                            }
                        }) {
                        }.start();
                        return;
                    }
                    pump(inputStream, handler, maxChunkSize);
                    return;
                }
                throw new IllegalArgumentException("Invalid max chunk size argument");
            }
            throw new IllegalArgumentException("Invalid handler argument");
        } else {
            throw new IllegalArgumentException("Invalid stream argument");
        }
    }

    /* access modifiers changed from: private */
    public void pump(TiStream inputStream, KrollFunction handler, int maxChunkSize) {
        int bytesRead;
        int totalBytesRead = 0;
        String errorDescription = "";
        do {
            try {
                BufferProxy buffer = new BufferProxy(maxChunkSize);
                bytesRead = inputStream.read(new Object[]{buffer, Integer.valueOf(0), Integer.valueOf(maxChunkSize)});
                if (bytesRead != -1) {
                    totalBytesRead += bytesRead;
                }
                if (bytesRead != buffer.getLength()) {
                    if (bytesRead == -1) {
                        buffer.resize(0);
                    } else {
                        buffer.resize(bytesRead);
                    }
                }
                handler.call(getKrollObject(), (HashMap) buildPumpCallbackArgs(inputStream, buffer, bytesRead, totalBytesRead, 0, errorDescription));
            } catch (IOException e) {
                TiStream tiStream = inputStream;
                KrollFunction krollFunction = handler;
                krollFunction.call(getKrollObject(), (HashMap) buildPumpCallbackArgs(tiStream, new BufferProxy(), 0, totalBytesRead, 1, e.getMessage()));
                return;
            }
        } while (bytesRead != -1);
    }

    /* access modifiers changed from: private */
    public KrollDict buildRWCallbackArgs(TiStream sourceStream, int bytesProcessed, int errorState, String errorDescription) {
        KrollDict callbackArgs = new KrollDict();
        callbackArgs.put("source", sourceStream);
        callbackArgs.put("bytesProcessed", Integer.valueOf(bytesProcessed));
        callbackArgs.put("errorState", Integer.valueOf(errorState));
        callbackArgs.put("errorDescription", errorDescription);
        callbackArgs.putCodeAndMessage(errorState, errorDescription);
        return callbackArgs;
    }

    /* access modifiers changed from: private */
    public KrollDict buildWriteStreamCallbackArgs(TiStream fromStream, TiStream toStream, int bytesProcessed, int errorState, String errorDescription) {
        KrollDict callbackArgs = new KrollDict();
        callbackArgs.put("fromStream", fromStream);
        callbackArgs.put("toStream", toStream);
        callbackArgs.put("bytesProcessed", Integer.valueOf(bytesProcessed));
        callbackArgs.put("errorState", Integer.valueOf(errorState));
        callbackArgs.put("errorDescription", errorDescription);
        callbackArgs.putCodeAndMessage(errorState, errorDescription);
        return callbackArgs;
    }

    private KrollDict buildPumpCallbackArgs(TiStream sourceStream, BufferProxy buffer, int bytesProcessed, int totalBytesProcessed, int errorState, String errorDescription) {
        KrollDict callbackArgs = new KrollDict();
        callbackArgs.put("source", sourceStream);
        callbackArgs.put("buffer", buffer);
        callbackArgs.put("bytesProcessed", Integer.valueOf(bytesProcessed));
        callbackArgs.put("totalBytesProcessed", Integer.valueOf(totalBytesProcessed));
        callbackArgs.put("errorState", Integer.valueOf(errorState));
        callbackArgs.put("errorDescription", errorDescription);
        callbackArgs.putCodeAndMessage(errorState, errorDescription);
        return callbackArgs;
    }

    public String getApiName() {
        return "Ti.Stream";
    }
}

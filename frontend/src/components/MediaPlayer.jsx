import React, { useRef, useEffect } from 'react';

const MediaPlayer = ({ src, type, seekTime }) => {
  const mediaRef = useRef(null);

  useEffect(() => {
    if (mediaRef.current && seekTime !== null && seekTime !== undefined) {
      mediaRef.current.currentTime = seekTime;
      mediaRef.current.play(); // Auto-play when seeking
    }
  }, [seekTime]);

  if (!src) {
    return (
      <div className="flex-1 flex items-center justify-center text-slate-500 border-2 border-dashed border-slate-700 rounded-lg">
        Select a media file or PDF to view content.
      </div>
    );
  }

  const isVideo = type && type.startsWith('video/');
  const isPdf = type && type.startsWith('application/pdf');

  if (isPdf) {
    return (
      <div className="flex flex-col h-full bg-slate-800 rounded-lg border border-slate-700 p-4">
        <h3 className="text-sm font-semibold mb-2 text-slate-400">PDF Viewer</h3>
        <iframe 
          src={src} 
          className="w-full h-full rounded border border-slate-600"
          title="PDF Document"
        />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full bg-slate-800 rounded-lg border border-slate-700 p-4">
      <h3 className="text-sm font-semibold mb-2 text-slate-400">
        {isVideo ? 'Video Player' : 'Audio Player'}
      </h3>
      <div className="flex-1 flex items-center justify-center bg-slate-900 rounded p-4">
        {isVideo ? (
          <video 
            ref={mediaRef} 
            src={src} 
            controls 
            className="w-full max-h-full rounded"
          />
        ) : (
          <div className="flex flex-col items-center space-y-6 w-full max-w-md">
            <div className="w-32 h-32 bg-purple-600/10 rounded-full flex items-center justify-center text-purple-400 border border-purple-500/20 shadow-lg shadow-purple-500/5">
              <svg className="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3" />
              </svg>
            </div>
            <div className="text-slate-400 text-sm font-medium">Playing Audio Content</div>
            <audio 
              ref={mediaRef} 
              src={src} 
              controls 
              className="w-full"
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default MediaPlayer;

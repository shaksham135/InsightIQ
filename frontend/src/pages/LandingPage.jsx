import React from 'react';
import { Link } from 'react-router-dom';

const LandingPage = () => {
  return (
    <div className="min-h-screen bg-slate-900 text-white flex flex-col justify-center items-center p-6">
      <div className="text-center max-w-4xl">
        <h1 className="text-6xl font-bold mb-6 bg-gradient-to-r from-purple-400 to-pink-600 text-transparent bg-clip-text">
          InsightIQ AI
        </h1>
        <p className="text-xl text-slate-300 mb-8">
          Interact with your documents, audio, and video files like never before. 
          Extract text, generate summaries, and jump to specific moments with AI.
        </p>
        <Link 
          to="/dashboard" 
          className="bg-purple-600 hover:bg-purple-700 text-white font-bold py-3 px-8 rounded-full text-lg transition duration-300 ease-in-out transform hover:scale-105"
        >
          Get Started
        </Link>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-20 max-w-6xl">
        <div className="bg-slate-800 p-6 rounded-lg border border-slate-700">
          <h3 className="text-xl font-semibold mb-3 text-purple-400">Multi-Format Upload</h3>
          <p className="text-slate-400">Support for PDFs, MP3s, WAVs, and MP4 files. Process all your content in one place.</p>
        </div>
        <div className="bg-slate-800 p-6 rounded-lg border border-slate-700">
          <h3 className="text-xl font-semibold mb-3 text-pink-400">AI-Powered Chat</h3>
          <p className="text-slate-400">Ask questions and get contextual answers directly from your uploaded files.</p>
        </div>
        <div className="bg-slate-800 p-6 rounded-lg border border-slate-700">
          <h3 className="text-xl font-semibold mb-3 text-blue-400">Timestamp Navigation</h3>
          <p className="text-slate-400">Click on AI-generated topics to jump directly to that moment in the audio or video.</p>
        </div>
      </div>
    </div>
  );
};

export default LandingPage;

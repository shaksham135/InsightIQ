import React, { useState, useEffect } from 'react';
import axios from 'axios';
import FileUpload from '../components/FileUpload';
import ChatInterface from '../components/ChatInterface';
import MediaPlayer from '../components/MediaPlayer';

const Dashboard = () => {
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [seekTime, setSeekTime] = useState(null);
  const [summary, setSummary] = useState('');
  const [loadingSummary, setLoadingSummary] = useState(false);
  const [timestamps, setTimestamps] = useState([]);
  const [loadingTimestamps, setLoadingTimestamps] = useState(false);
  const [backendStatus, setBackendStatus] = useState('checking'); // 'checking', 'connected', 'error', ''

  useEffect(() => {
    fetchFiles();
  }, []);

  useEffect(() => {
    setSummary('');
    setTimestamps([]);
    if (selectedFile) {
      fetchSummary(selectedFile.id);
      fetchTimestamps(selectedFile.id);
    }
  }, [selectedFile]);

  const fetchSummary = async (fileId) => {
    setLoadingSummary(true);
    try {
      const response = await axios.get(`/api/files/${fileId}/summary`);
      if (response.data.success) {
        setSummary(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching summary:', error);
      setSummary('Failed to load summary.');
    } finally {
      setLoadingSummary(false);
    }
  };

  const fetchTimestamps = async (fileId) => {
    setLoadingTimestamps(true);
    try {
      const response = await axios.get(`/api/files/${fileId}/timestamp`);
      if (response.data.success) {
        const data = response.data.data;
        const parsed = typeof data === 'string' ? JSON.parse(data) : data;
        setTimestamps(parsed.segments || []);
      }
    } catch (error) {
      console.error('Error fetching timestamps:', error);
      setTimestamps([]);
    } finally {
      setLoadingTimestamps(false);
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const fetchFiles = async () => {
    setBackendStatus('checking');
    try {
      const response = await axios.get('/api/files');
      if (response.data.success) {
        setFiles(response.data.data);
        setBackendStatus('connected');
        setTimeout(() => setBackendStatus(''), 5000); // Hide after 5s
      }
    } catch (error) {
      console.error('Error fetching files:', error);
      setBackendStatus('error');
    }
  };

  const handleUploadSuccess = (newFile) => {
    setFiles([...files, newFile]);
    setSelectedFile(newFile);
  };

  return (
    <div className="min-h-screen bg-slate-900 text-white flex flex-col">
      {backendStatus === 'checking' && (
        <div className="bg-yellow-600/80 text-white text-center py-2 text-sm font-medium backdrop-blur-sm">
          🚀 Powering up the AI engine... Hang tight, our backend is stretching and waking up! (Takes about a minute on the free tier)
        </div>
      )}
      {backendStatus === 'connected' && (
        <div className="bg-green-600/80 text-white text-center py-2 text-sm font-medium backdrop-blur-sm">
          Successfully connected to backend!
        </div>
      )}
      {backendStatus === 'error' && (
        <div className="bg-red-600/80 text-white text-center py-2 text-sm font-medium backdrop-blur-sm">
          Failed to connect to backend. Please refresh or check if backend is running.
        </div>
      )}

      <div className="flex-1 flex flex-col lg:flex-row overflow-hidden">
        {/* Sidebar */}
        <div className="w-full lg:w-64 bg-slate-800 border-b lg:border-b-0 lg:border-r border-slate-700 p-4 flex flex-col">
        <h2 className="text-2xl font-bold mb-6 text-purple-400">InsightIQ</h2>
        
        <FileUpload onUploadSuccess={handleUploadSuccess} />

        <div className="flex-1 overflow-x-auto lg:overflow-y-auto mt-6">
          <h3 className="text-sm uppercase text-slate-500 mb-2 font-semibold">My Files</h3>
          <div className="flex flex-row lg:flex-col space-x-2 lg:space-x-0 lg:space-y-2 pb-2 lg:pb-0">
            {files.map(file => (
              <div 
                key={file.id} 
                onClick={() => setSelectedFile(file)}
                className={`p-2 rounded cursor-pointer transition duration-200 flex-shrink-0 w-48 lg:w-full ${
                  selectedFile && selectedFile.id === file.id 
                    ? 'bg-purple-600 text-white' 
                    : 'bg-slate-700 hover:bg-slate-600 text-slate-200'
                }`}
              >
                <div className="font-medium truncate">{file.fileName}</div>
                <div className="text-xs text-slate-400 truncate">{file.fileType}</div>
              </div>
            ))}
            {files.length === 0 && (
              <div className="text-slate-400 text-sm italic">No files uploaded yet.</div>
            )}
          </div>
        </div>
        <div className="mt-auto border-t border-slate-700 pt-4 text-sm text-slate-500">
          InsightIQ AI v1.0
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col">
        {/* Header */}
        <div className="bg-slate-800 p-4 border-b border-slate-700 flex justify-between items-center">
          <h1 className="text-xl font-semibold truncate">
            {selectedFile ? selectedFile.fileName : 'Workspace'}
          </h1>
          <div className="flex items-center space-x-4">
            <span className="bg-slate-700 px-3 py-1 rounded-full text-sm">Guest Mode</span>
          </div>
        </div>

        {/* Workspace */}
        <div className="flex-1 p-6 grid grid-cols-1 lg:grid-cols-2 gap-6 overflow-hidden">
          {/* Left Column: File Details & Media Player */}
          <div className="bg-slate-800 p-4 rounded-lg border border-slate-700 flex flex-col h-full">
            <div className="flex-1 overflow-hidden">
              <MediaPlayer 
                src={selectedFile ? selectedFile.filePath : null} 
                type={selectedFile ? selectedFile.fileType : null} 
                seekTime={seekTime}
              />
            </div>
            
            {/* Summary Section */}
            <div className="mt-4 border-t border-slate-700 pt-4 overflow-y-auto max-h-40">
              <h3 className="text-sm font-semibold mb-2 text-pink-300">AI Summary</h3>
              {loadingSummary ? (
                <div className="text-slate-400 text-sm">Generating summary...</div>
              ) : (
                <p className="text-slate-300 text-sm whitespace-pre-wrap">{summary || 'No summary available.'}</p>
              )}
            </div>

            {/* Timestamps Section */}
            <div className="mt-4 border-t border-slate-700 pt-4 overflow-y-auto max-h-40">
              <h3 className="text-sm font-semibold mb-2 text-pink-300">Timestamps</h3>
              {loadingTimestamps ? (
                <div className="text-slate-400 text-sm">Loading timestamps...</div>
              ) : timestamps.length > 0 ? (
                <div className="space-y-2">
                  {timestamps.map((segment) => (
                    <div 
                      key={segment.id} 
                      onClick={() => setSeekTime(segment.start)}
                      className="text-sm text-slate-300 hover:text-white cursor-pointer flex items-start space-x-2 p-1 rounded hover:bg-slate-700"
                    >
                      <span className="font-mono text-purple-400 bg-slate-900 px-1.5 py-0.5 rounded text-xs">
                        {formatTime(segment.start)}
                      </span>
                      <span>{segment.text}</span>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-slate-500 text-sm italic">No timestamps available.</div>
              )}
            </div>
          </div>

          {/* Right Column: AI Assistant (Chat) */}
          <div className="bg-slate-800 p-4 rounded-lg border border-slate-700 flex flex-col h-full">
            <h2 className="text-lg font-semibold mb-4 text-pink-300">AI Assistant</h2>
            <div className="flex-1 overflow-hidden">
              <ChatInterface fileId={selectedFile ? selectedFile.id : null} />
            </div>
          </div>
        </div>
      </div>
      </div>
    </div>
  );
};

export default Dashboard;

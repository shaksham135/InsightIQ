import React, { useState } from 'react';
import axios from 'axios';

const FileUpload = ({ onUploadSuccess }) => {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
    setMessage('');
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage('Please select a file first.');
      return;
    }

    setLoading(true);
    setMessage('Uploading and processing...');

    const formData = new FormData();
    formData.append('file', file);

    try {
      // Note: In production/dev, you might need to specify the full URL if proxy is not set up.
      // Assuming Vite proxy or same domain.
      const response = await axios.post('/api/files/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.data.success) {
        setMessage('File uploaded successfully!');
        if (onUploadSuccess) {
          onUploadSuccess(response.data.data);
        }
        setFile(null); // Reset file input
      } else {
        setMessage('Upload failed: ' + response.data.message);
      }
    } catch (error) {
      setMessage('Error uploading file: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4 bg-slate-800 rounded-lg border border-slate-700">
      <div className="flex flex-col items-center justify-center border-2 border-dashed border-slate-600 rounded-lg p-6 hover:border-purple-500 transition duration-200">
        <input 
          type="file" 
          onChange={handleFileChange} 
          className="hidden" 
          id="file-upload"
          accept=".pdf,.mp3,.wav,.mp4"
        />
        <label 
          htmlFor="file-upload" 
          className="cursor-pointer text-center"
        >
          <svg className="mx-auto h-12 w-12 text-slate-500" stroke="currentColor" fill="none" viewBox="0 0 48 48" aria-hidden="true">
            <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          <div className="mt-4 flex text-sm text-slate-400">
            <span className="relative cursor-pointer bg-slate-700 rounded-md font-medium text-purple-400 hover:text-purple-300 focus-within:outline-none focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-purple-500 px-2 py-1">
              Upload a file
            </span>
            <p className="pl-1">or drag and drop</p>
          </div>
          <p className="text-xs text-slate-500 mt-2">
            PDF, MP3, WAV, MP4 up to 25MB
          </p>
        </label>
      </div>

      {file && (
        <div className="mt-4 text-sm text-slate-300 flex justify-between items-center">
          <span>Selected: <span className="font-semibold">{file.name}</span></span>
          <button 
            onClick={handleUpload} 
            disabled={loading}
            className={`py-1 px-4 rounded font-semibold transition duration-200 ${
              loading 
                ? 'bg-slate-600 cursor-not-allowed' 
                : 'bg-purple-600 hover:bg-purple-700 text-white'
            }`}
          >
            {loading ? 'Processing...' : 'Upload'}
          </button>
        </div>
      )}

      {message && (
        <div className={`mt-3 text-sm p-2 rounded ${
          message.includes('success') 
            ? 'bg-green-900/50 text-green-400 border border-green-800' 
            : 'bg-slate-700 text-slate-300 border border-slate-600'
        }`}>
          {message}
        </div>
      )}
    </div>
  );
};

export default FileUpload;

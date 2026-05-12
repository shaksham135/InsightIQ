import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ChatInterface = ({ fileId }) => {
  const [messages, setMessages] = useState([
    { role: 'assistant', content: 'Hello! I can answer questions about your uploaded file. Ask me anything!' }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setMessages([
      { role: 'assistant', content: 'Hello! I can answer questions about your uploaded file. Ask me anything!' }
    ]);
    setInput('');
  }, [fileId]);

  const handleSend = async () => {
    if (!input.trim() || !fileId) return;

    const userMessage = { role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const response = await axios.post('/api/chat/ask', {
        fileId: fileId,
        question: input
      });

      if (response.data.success) {
        setMessages(prev => [...prev, { role: 'assistant', content: response.data.data }]);
      } else {
        setMessages(prev => [...prev, { role: 'assistant', content: 'Error: ' + response.data.message }]);
      }
    } catch (error) {
      setMessages(prev => [...prev, { role: 'assistant', content: 'Error calling AI: ' + error.message }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col h-full bg-slate-800 rounded-lg border border-slate-700 p-4">
      {/* Messages */}
      <div className="flex-1 overflow-y-auto space-y-4 mb-4 p-2">
        {messages.map((msg, index) => (
          <div key={index} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-[80%] p-3 rounded-lg ${
              msg.role === 'user' 
                ? 'bg-purple-600 text-white rounded-br-none' 
                : 'bg-slate-700 text-slate-200 rounded-bl-none border border-slate-600'
            }`}>
              <p className="text-sm whitespace-pre-wrap">{msg.content}</p>
            </div>
          </div>
        ))}
        {loading && (
          <div className="flex justify-start">
            <div className="bg-slate-700 text-slate-400 p-3 rounded-lg rounded-bl-none border border-slate-600 flex items-center space-x-2">
              <span>Thinking</span>
              <span className="flex space-x-1">
                <span className="h-1.5 w-1.5 bg-slate-400 rounded-full animate-bounce" style={{ animationDelay: '0s' }}></span>
                <span className="h-1.5 w-1.5 bg-slate-400 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></span>
                <span className="h-1.5 w-1.5 bg-slate-400 rounded-full animate-bounce" style={{ animationDelay: '0.4s' }}></span>
              </span>
            </div>
          </div>
        )}
      </div>

      {/* Input */}
      <div className="mt-auto flex space-x-2">
        <input 
          type="text" 
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSend()}
          placeholder={fileId ? "Ask a question..." : "Select a file first to chat"}
          disabled={!fileId || loading}
          className="flex-1 bg-slate-700 text-white p-3 rounded-lg border border-slate-600 focus:outline-none focus:border-purple-500 disabled:bg-slate-800 disabled:text-slate-600 disabled:cursor-not-allowed"
        />
        <button 
          onClick={handleSend} 
          disabled={!fileId || loading || !input.trim()}
          className={`px-6 rounded-lg font-semibold transition duration-200 ${
            !fileId || loading || !input.trim()
              ? 'bg-slate-600 cursor-not-allowed text-slate-400' 
              : 'bg-purple-600 hover:bg-purple-700 text-white'
          }`}
        >
          Send
        </button>
      </div>
    </div>
  );
};

export default ChatInterface;

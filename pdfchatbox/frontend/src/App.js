import React, { useState } from "react";

function App() {
  // Upload-related state
  const [selectedFile, setSelectedFile] = useState(null);
  const [pdfId, setPdfId] = useState("");
  const [fileName, setFileName] = useState("");
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState("");

  // Chat-related state
  const [messages, setMessages] = useState([
    { sender: "bot", text: "Hi! Upload a PDF and ask me anything from it 😊" },
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [chatError, setChatError] = useState("");

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    setSelectedFile(file || null);
    setFileName(file ? file.name : "");
    setUploadError("");
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setUploadError("Please select a PDF file first.");
      return;
    }

    setUploading(true);
    setUploadError("");

    try {
      const formData = new FormData();
      formData.append("file", selectedFile);

      const response = await fetch("http://localhost:8080/api/pdf/upload", {
        method: "POST",
        body: formData,
      });

      if (!response.ok) {
        throw new Error("Failed to upload PDF");
      }

      const data = await response.json();
      setPdfId(data.pdfId);
      setFileName(data.fileName || selectedFile.name);

      setMessages((prev) => [
        ...prev,
        {
          sender: "bot",
          text: `PDF "${data.fileName || selectedFile.name}" uploaded successfully! Now you can ask questions.`,
        },
      ]);
    } catch (err) {
      console.error(err);
      setUploadError("Upload failed. Please try again.");
    } finally {
      setUploading(false);
    }
  };

  const handleSend = async () => {
    if (!input.trim() || !pdfId) return;

    const question = input.trim();
    const userMessage = { sender: "user", text: question };
    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setLoading(true);
    setChatError("");

    try {
      const response = await fetch("http://localhost:8080/api/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          pdfId: pdfId,
          question: question,
        }),
      });
      

      if (!response.ok) {
        throw new Error("Failed to get response from server");
      }

      const data = await response.json();
      const botMessage = {
        sender: "bot",
        text: data.answer || "No answer received from server.",
      };

      setMessages((prev) => [...prev, botMessage]);
    } catch (err) {
      console.error(err);
      setChatError("Something went wrong while getting the answer.");
      const botMessage = {
        sender: "bot",
        text: "Sorry, I couldn't process your question.",
      };
      setMessages((prev) => [...prev, botMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        background: "#0f172a",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "20px",
        boxSizing: "border-box",
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: "1000px",
          background: "#f9fafb",
          borderRadius: "16px",
          boxShadow: "0 15px 40px rgba(0,0,0,0.25)",
          overflow: "hidden",
          display: "flex",
          flexDirection: "column",
          minHeight: "80vh",
        }}
      >
        {/* Header */}
        <div
          style={{
            padding: "16px 20px",
            borderBottom: "1px solid #e5e7eb",
            background:
              "linear-gradient(135deg, rgba(37,99,235,0.95), rgba(79,70,229,0.95))",
            color: "white",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <div>
            <div style={{ fontSize: "20px", fontWeight: 700 }}>PDF Chatbox</div>
            <div style={{ fontSize: "12px", opacity: 0.9 }}>
              Upload a PDF and chat with it in real time.
            </div>
          </div>
          <div style={{ textAlign: "right", fontSize: "12px" }}>
            <div>Status: {loading ? "Thinking…" : "Online"}</div>
            <div style={{ opacity: 0.8 }}>
              {pdfId ? `PDF Loaded: ${fileName}` : "No PDF selected"}
            </div>
          </div>
        </div>

        {/* Upload Section */}
        <div
          style={{
            padding: "12px 16px",
            borderBottom: "1px solid #e5e7eb",
            background: "#ffffff",
            display: "flex",
            flexWrap: "wrap",
            alignItems: "center",
            gap: "10px",
          }}
        >
          <div style={{ flex: 1, minWidth: "200px" }}>
            <label
              htmlFor="pdf-upload"
              style={{
                display: "inline-block",
                padding: "8px 12px",
                borderRadius: "999px",
                border: "1px dashed #94a3b8",
                cursor: "pointer",
                fontSize: "14px",
              }}
            >
              📄 Choose PDF
            </label>
            <input
              id="pdf-upload"
              type="file"
              accept="application/pdf"
              style={{ display: "none" }}
              onChange={handleFileChange}
            />
            <span
              style={{
                marginLeft: "10px",
                fontSize: "13px",
                color: "#4b5563",
              }}
            >
              {fileName || "No file selected"}
            </span>
          </div>

          <button
            onClick={handleUpload}
            disabled={!selectedFile || uploading}
            style={{
              padding: "8px 16px",
              borderRadius: "999px",
              border: "none",
              fontSize: "14px",
              fontWeight: 500,
              cursor: !selectedFile || uploading ? "not-allowed" : "pointer",
              opacity: !selectedFile || uploading ? 0.6 : 1,
              background: "#2563eb",
              color: "white",
              whiteSpace: "nowrap",
            }}
          >
            {uploading ? "Uploading…" : "Upload PDF"}
          </button>

          {uploadError && (
            <div
              style={{
                width: "100%",
                fontSize: "12px",
                color: "#b91c1c",
                marginTop: "4px",
              }}
            >
              {uploadError}
            </div>
          )}
        </div>

        {/* Main chat area */}
        <div
          style={{
            flex: 1,
            display: "flex",
            flexDirection: "column",
            padding: "12px 16px",
            background: "#f3f4f6",
          }}
        >
          {/* Messages */}
          <div
            style={{
              flex: 1,
              overflowY: "auto",
              paddingRight: "4px",
              marginBottom: "8px",
            }}
          >
            {messages.map((msg, index) => (
              <div
                key={index}
                style={{
                  display: "flex",
                  justifyContent:
                    msg.sender === "user" ? "flex-end" : "flex-start",
                  marginBottom: "8px",
                }}
              >
                <div
                  style={{
                    maxWidth: "75%",
                    padding: "10px 12px",
                    borderRadius:
                      msg.sender === "user"
                        ? "14px 14px 4px 14px"
                        : "14px 14px 14px 4px",
                    whiteSpace: "pre-wrap",
                    fontSize: "14px",
                    background:
                      msg.sender === "user" ? "#2563eb" : "#ffffff",
                    color: msg.sender === "user" ? "white" : "#111827",
                    boxShadow: "0 1px 3px rgba(0,0,0,0.1)",
                  }}
                >
                  {msg.text}
                </div>
              </div>
            ))}

            {loading && (
              <div
                style={{
                  fontSize: "13px",
                  color: "#6b7280",
                  marginTop: "4px",
                }}
              >
                Bot is typing…
              </div>
            )}

            {chatError && (
              <div
                style={{
                  marginTop: "6px",
                  fontSize: "12px",
                  color: "#b91c1c",
                }}
              >
                {chatError}
              </div>
            )}
          </div>

          {/* Input box */}
          <div
            style={{
              borderTop: "1px solid #e5e7eb",
              paddingTop: "8px",
            }}
          >
            <textarea
              rows={2}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder={
                pdfId
                  ? "Ask something about this PDF..."
                  : "Upload a PDF first, then ask your question..."
              }
              style={{
                width: "100%",
                resize: "none",
                borderRadius: "10px",
                border: "1px solid #cbd5f5",
                padding: "8px 10px",
                fontSize: "14px",
                outline: "none",
                boxSizing: "border-box",
                marginBottom: "8px",
              }}
            />

            <div
              style={{
                display: "flex",
                justifyContent: "flex-end",
              }}
            >
              <button
                onClick={handleSend}
                disabled={!input.trim() || !pdfId || loading}
                style={{
                  padding: "8px 16px",
                  borderRadius: "999px",
                  border: "none",
                  fontSize: "14px",
                  fontWeight: 500,
                  cursor:
                    !input.trim() || !pdfId || loading
                      ? "not-allowed"
                      : "pointer",
                  opacity:
                    !input.trim() || !pdfId || loading ? 0.6 : 1,
                  background: "#22c55e",
                  color: "white",
                }}
              >
                {loading ? "Sending…" : "Send"}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;


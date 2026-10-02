/**
 * 基于 fetch 的 SSE（Server-Sent Events）流式请求工具
 *
 * 适用场景：后端 GET + text/event-stream 接口（本项目两个聊天接口均为该形式），
 * 相比 EventSource 不会自动重连，且能感知流的正常结束，便于前端控制"停止生成"。
 *
 * @param {string} url 完整请求地址（含查询参数）
 * @param {Object} handlers 回调集合
 * @param {(data: string) => void} handlers.onMessage 每收到一个 SSE 事件的 data 时回调
 * @param {(err: Error) => void} [handlers.onError] 请求或读取失败时回调
 * @param {() => void} [handlers.onDone] 流正常结束（或被主动中断）时回调
 * @param {AbortSignal} [handlers.signal] 用于中断请求
 */
export async function fetchSSE(url, { onMessage, onError, onDone, signal } = {}) {
  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: { Accept: 'text/event-stream' },
      signal
    })
    if (!response.ok) {
      throw new Error(`请求失败：HTTP ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })

      // SSE 以空行分隔事件，最后一段可能不完整，留在 buffer 等待下一帧
      const events = buffer.split('\n\n')
      buffer = events.pop()

      for (const event of events) {
        const data = parseEventData(event)
        if (data !== null && onMessage) {
          onMessage(data)
        }
      }
    }
    onDone && onDone()
  } catch (err) {
    // 主动中断视为正常结束
    if (err.name === 'AbortError') {
      onDone && onDone()
      return
    }
    onError && onError(err)
  }
}

/**
 * 解析单个 SSE 事件块，返回 data 内容；无 data 行时返回 null
 * 事件内多行 data 按规范用换行拼接（后端内容含 \n 时会被拆成多行 data）
 */
function parseEventData(event) {
  const dataLines = []
  for (const line of event.split('\n')) {
    // 仅处理 data: 行，忽略 event:/id:/retry: 与冒号开头的注释行
    if (line.startsWith('data:')) {
      // 去掉 "data:" 后最多一个前导空格
      dataLines.push(line.slice(5).replace(/^ /, ''))
    }
  }
  return dataLines.length > 0 ? dataLines.join('\n') : null
}

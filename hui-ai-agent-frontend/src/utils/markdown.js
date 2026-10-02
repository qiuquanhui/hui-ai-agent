/**
 * 轻量 Markdown 渲染器（无第三方依赖，用于 AI 回复的富文本展示）
 *
 * 安全策略：先整体转义 HTML（& < >），再做有限的 Markdown 转换，
 * 因此即使回复中包含 HTML 片段也不会被执行，可安全用于 v-html
 *
 * 支持语法：代码块 ```、行内代码、标题 #、无序/有序列表、粗体、斜体、链接、段落
 */

export function renderMarkdown(src) {
  if (!src) return ''

  let text = escapeHtml(src)

  // 1. 先摘出代码块，避免内部内容被其它规则二次处理
  const codeBlocks = []
  text = text.replace(/```[^\n]*\n?([\s\S]*?)(?:```|$)/g, (_m, code) => {
    codeBlocks.push(`<pre><code>${code.replace(/\n$/, '')}</code></pre>`)
    return `\u0000B${codeBlocks.length - 1}\u0000`
  })

  // 2. 逐行解析块级结构（标题 / 列表 / 段落）
  const out = []
  let paragraph = []
  let listTag = null

  const flushParagraph = () => {
    if (paragraph.length > 0) {
      out.push(`<p>${paragraph.join('<br>')}</p>`)
      paragraph = []
    }
  }
  const closeList = () => {
    if (listTag) {
      out.push(`</${listTag}>`)
      listTag = null
    }
  }

  for (const rawLine of text.split('\n')) {
    const line = rawLine.trim()
    if (!line) {
      flushParagraph()
      closeList()
      continue
    }

    // 占位符行（代码块）单独成块
    if (/^\u0000B\d+\u0000$/.test(line)) {
      flushParagraph()
      closeList()
      out.push(line)
      continue
    }

    let m = line.match(/^(#{1,4})\s+(.*)$/)
    if (m) {
      flushParagraph()
      closeList()
      const level = Math.min(m[1].length + 2, 6) // 聊天气泡内标题降级，避免过大
      out.push(`<h${level}>${renderInline(m[2])}</h${level}>`)
      continue
    }

    m = line.match(/^[-*•·]\s+(.+)$/)
    if (m) {
      flushParagraph()
      if (listTag !== 'ul') {
        closeList()
        out.push('<ul>')
        listTag = 'ul'
      }
      out.push(`<li>${renderInline(m[1])}</li>`)
      continue
    }

    m = line.match(/^\d+[.、)）]\s+(.+)$/)
    if (m) {
      flushParagraph()
      if (listTag !== 'ol') {
        closeList()
        out.push('<ol>')
        listTag = 'ol'
      }
      out.push(`<li>${renderInline(m[1])}</li>`)
      continue
    }

    paragraph.push(renderInline(line))
  }
  flushParagraph()
  closeList()

  // 3. 还原代码块
  return out
    .join('\n')
    .replace(/\u0000B(\d+)\u0000/g, (_m, i) => codeBlocks[Number(i)])
}

/** 行内语法：粗体、斜体、链接、行内代码 */
function renderInline(s) {
  return (
    s
      .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
      .replace(/(^|[^*])\*([^*\n]+)\*(?!\*)/g, '$1<em>$2</em>')
      .replace(/`([^`\n]+)`/g, '<code>$1</code>')
      // 链接仅允许 http(s)，剔除可能破坏属性的字符
      .replace(
        /\[([^\]]+)\]\((https?:\/\/[^)\s]+)\)/g,
        (_m, label, url) => `<a href="${url.replace(/["'<>\s]/g, '')}" target="_blank" rel="noopener noreferrer">${label}</a>`
      )
  )
}

function escapeHtml(s) {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

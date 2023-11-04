import MonacoEditor ,{ loader} from '@monaco-editor/react';
import monacoUrl from '/monaco/min/vs?url'
loader.config({ paths: { vs: monacoUrl } });

interface EditorProps {
    code: string;
    onChange: (code: string | undefined) => void
}

// const EditorOptions = {
//     acceptSuggestionOnCommitCharacter: true,
//     acceptSuggestionOnEnter: 'on',
//     accessibilitySupport: 'auto',
//     autoIndent: true,Ï
//     automaticLayout: true,
//     codeLens: true,
//     colorDecorators: true,
//     contextmenu: true,
//     cursorBlinking: 'blink',
//     cursorSmoothCaretAnimation: false,
//     cursorStyle: 'line',
//     disableLayerHinting: false,
//     disableMonospaceOptimizations: false,
//     dragAndDrop: false,
//     fixedOverflowWidgets: false,
//     folding: true,
//     foldingStrategy: 'auto',
//     fontLigatures: false,
//     formatOnPaste: false,
//     formatOnType: false,
//     hideCursorInOverviewRuler: false,
//     highlightActiveIndentGuide: true,
//     links: true,
//     minimap: {
//         enabled: false,
//     },
//     mouseWheelZoom: false,
//     multiCursorMergeOverlapping: true,
//     multiCursorModifier: 'alt',
//     overviewRulerBorder: true,
//     overviewRulerLanes: 2,
//     quickSuggestions: true,
//     quickSuggestionsDelay: 100,
//     readOnly: false,
//     renderControlCharacters: false,
//     renderFinalNewline: "on",
//     renderIndentGuides: true,
//     renderLineHighlight: 'all',
//     renderWhitespace: 'none',
//     revealHorizontalRightPadding: 30,
//     roundedSelection: true,
//     rulers: [],
//     scrollBeyondLastColumn: 5,
//     scrollBeyondLastLine: true,
//     selectOnLineNumbers: true,
//     selectionClipboard: true,
//     selectionHighlight: true,
//     showFoldingControls: 'mouseover',
//     smoothScrolling: false,
//     suggestOnTriggerCharacters: true,
//     wordBasedSuggestions: true,
//     // eslint-disable-next-line
//     wordSeparators: `~!@#$%^&*()-=+[{]}\|;:'",.<>/?`,
//     wordWrap: 'off',
//     wordWrapBreakAfterCharacters: '\t})]?|&,;',
//     wordWrapBreakBeforeCharacters: '{([+',
//     wordWrapBreakObtrusiveCharacters: '.',
//     wordWrapColumn: 80,
//     wordWrapMinified: true,
//     wrappingIndent: 'none',
// }

const Editor = (props: EditorProps) => {
    return (
        <div>
            <MonacoEditor
                theme={"Github"}
                height="55vh"
                value={props.code}
                defaultLanguage={"lua"}
                // options={EditorOptions}
                onChange={props.onChange}
            />
        </div>
    );
}

export default Editor;

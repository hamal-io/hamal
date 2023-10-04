import MonacoEditor from '@monaco-editor/react';

interface EditorProps {
    code: string;
    onChange: (code: string | undefined) => void
}

// const EditorOptions = {
//     acceptSuggestionOnCommitCharacter: true,
//     acceptSuggestionOnEnter: 'on',
//     accessibilitySupport: 'auto',
//     autoIndent: true,
//     automaticLayout: true,
//     codeLens: true,
//     colorDecorators: true,
//     contextmenu: true,
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
//     wordWrapBreakAfterCharacters: '\t})]?|&,;',
//     wordWrapBreakBeforeCharacters: '{([+',
//     wordWrapBreakObtrusiveCharacters: '.',
//     wordWrapColumn: 80,
//     wordWrapMinified: true,
// }

const Editor = (props: EditorProps) => {
    return (
        <MonacoEditor
            theme={"Github"}
            height="55vh"
            defaultValue={props.code}
            defaultLanguage={"lua"}
            onChange={props.onChange}
        />
    );
}

export default Editor;

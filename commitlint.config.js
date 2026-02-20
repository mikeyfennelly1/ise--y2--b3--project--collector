module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'body-max-line-length': [0],
    'header-max-length': [2, 'always', 120],
    'subject-case': [
      2,
      'always',
      ['lower-case', 'kebab-case', 'start-case', 'sentence-case'],
    ],
  },
};

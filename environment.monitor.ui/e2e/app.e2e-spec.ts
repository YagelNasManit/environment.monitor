import {Environment
.
Monitor.UiPage
}
from
'./app.po';

describe('environment.monitor.ui App', () => {
  let page: Environment.Monitor.UiPage;

  beforeEach(() => {
    page = new Environment.Monitor.UiPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!');
  });
});

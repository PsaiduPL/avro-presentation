// Prosty silnik slajdów: skalowanie 1600×900, nawigacja klawiaturą, fragmenty.
(() => {
  const stage = document.querySelector('.stage');
  const slides = [...document.querySelectorAll('.slide')];
  const navLinks = [...document.querySelectorAll('.topnav a[data-go]')];
  const counter = document.querySelector('.counter');
  const progress = document.querySelector('.progress');
  let current = 0;

  const pad = (n) => String(n).padStart(2, '0');
  const fragments = (i) => [...slides[i].querySelectorAll('.fragment')];

  function fit() {
    const scale = Math.min(window.innerWidth / 1600, window.innerHeight / 900);
    stage.style.transform = `translate(-50%, -50%) scale(${scale})`;
  }

  function show(i, revealAll = false) {
    current = Math.max(0, Math.min(slides.length - 1, i));
    slides.forEach((s, j) => s.classList.toggle('active', j === current));
    fragments(current).forEach((f) => f.classList.toggle('visible', revealAll));

    const section = slides[current].dataset.section;
    navLinks.forEach((a) => a.classList.toggle('current', a.dataset.section === section));
    counter.innerHTML = `<b>${pad(current + 1)}</b> / ${pad(slides.length)}`;
    progress.style.width = `${((current + 1) / slides.length) * 100}%`;
    history.replaceState(null, '', `#${current + 1}`);
  }

  function next() {
    const hidden = fragments(current).filter((f) => !f.classList.contains('visible'));
    if (hidden.length) hidden[0].classList.add('visible');
    else if (current < slides.length - 1) show(current + 1);
  }

  function prev() {
    const visible = fragments(current).filter((f) => f.classList.contains('visible'));
    if (visible.length) visible[visible.length - 1].classList.remove('visible');
    else if (current > 0) show(current - 1, true);
  }

  document.addEventListener('keydown', (e) => {
    if (e.metaKey || e.ctrlKey || e.altKey) return;
    switch (e.key) {
      case 'ArrowRight': case 'ArrowDown': case ' ': case 'PageDown': case 'Enter':
        e.preventDefault(); next(); break;
      case 'ArrowLeft': case 'ArrowUp': case 'PageUp': case 'Backspace':
        e.preventDefault(); prev(); break;
      case 'Home': show(0); break;
      case 'End': show(slides.length - 1, true); break;
      case 'f': case 'F':
        if (document.fullscreenElement) document.exitFullscreen();
        else document.documentElement.requestFullscreen();
        break;
    }
  });

  navLinks.forEach((a) => a.addEventListener('click', () => show(Number(a.dataset.go) - 1)));
  document.querySelector('.logo').addEventListener('click', () => show(0));
  window.addEventListener('resize', fit);
  window.addEventListener('hashchange', () => {
    const n = parseInt(location.hash.slice(1), 10);
    if (n && n - 1 !== current) show(n - 1);
  });

  // Gdy font Abril Fatface nie jest dostępny (offline), pogrub zastępczy font szeryfowy.
  document.fonts.load('40px "Abril Fatface"').finally(() => {
    const loaded = [...document.fonts].some(
      (f) => f.family.replace(/["']/g, '') === 'Abril Fatface' && f.status === 'loaded'
    );
    if (!loaded) document.body.classList.add('no-display-font');
  });

  fit();
  const start = parseInt(location.hash.slice(1), 10);
  show(start ? start - 1 : 0);
})();
